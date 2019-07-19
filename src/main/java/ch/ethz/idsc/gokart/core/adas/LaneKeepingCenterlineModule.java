// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitPlanner;
import ch.ethz.idsc.gokart.core.pure.CurveSe2PursuitLcmClient;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.TensorListener;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/**  */
public class LaneKeepingCenterlineModule extends AbstractClockedModule implements //
    GokartPoseListener, TensorListener {
  private static final Tensor OFS_L = Tensors.fromString("{0, +1[m], 0}").unmodifiable();
  private static final Tensor OFS_R = Tensors.fromString("{0, -1[m], 0}").unmodifiable();
  private static final Scalar PERIOD = Quantity.of(0.1, SI.SECOND);
  // ---
  private final CurveSe2PursuitLcmClient curveSe2PursuitLcmClient = new CurveSe2PursuitLcmClient();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final CurveClothoidPursuitPlanner curvePlannerL;
  private final CurveClothoidPursuitPlanner curvePlannerR;
  private SteerConfig steerConfig = new SteerConfig();
  // ---
 
  public GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  public Optional<Tensor> optionalCurve = Optional.empty();
  public Tensor laneBoundaryL;
  public Tensor laneBoundaryR;
  Optional<Clip> optionalPermittedRange = Optional.empty();
  Tensor velocity = GokartPoseEvents.motionlessUninitialized().getVelocity();

  public LaneKeepingCenterlineModule() {
    this(ClothoidPursuitConfig.GLOBAL);
  }

  public LaneKeepingCenterlineModule(ClothoidPursuitConfig _clothoidPursuitConfig) {
    ClothoidPursuitConfig clothoidPursuitConfig = _clothoidPursuitConfig;
    try {
      clothoidPursuitConfig = Serialization.copy(_clothoidPursuitConfig);
      // large value is a hack to get a solution
      clothoidPursuitConfig.turningRatioMax = Quantity.of(1000.0, SI.PER_METER);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    curvePlannerL = new CurveClothoidPursuitPlanner(clothoidPursuitConfig);
    curvePlannerR = new CurveClothoidPursuitPlanner(clothoidPursuitConfig);
  }

  @Override // from AbstractModule
  public void first() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    curveSe2PursuitLcmClient.addListener(this);
    curveSe2PursuitLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  public void last() {
    gokartPoseLcmClient.stopSubscriptions();
    curveSe2PursuitLcmClient.stopSubscriptions();
  }

  @Override // from AbstractClockedModule
  protected synchronized void runAlgo() {
    boolean isQualityOk = LocalizationConfig.GLOBAL.isQualityOk(gokartPoseEvent);
    Tensor pose = isQualityOk //
        ? gokartPoseEvent.getPose() //
        : GokartPoseEvents.motionlessUninitialized().getPose();
    velocity = isQualityOk //
        ? gokartPoseEvent.getVelocity() //
        : GokartPoseEvents.motionlessUninitialized().getVelocity();
    boolean isPresent = optionalCurve.isPresent();
    Tensor curve = isPresent //
        ? optionalCurve.get()//
        : null;
    System.out.println("isPresent: " + isPresent + "isQualityOK: " + isQualityOk);
    if (isPresent && isQualityOk) {
      optionalPermittedRange = getPermittedRange(curve, pose);
      System.out.println(optionalPermittedRange);
    }
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return PERIOD;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  synchronized void setCurve(Optional<Tensor> optional) {
    optionalCurve = optional;
    if (optional.isPresent()) {
      laneBoundaryL = Tensor.of(optional.get().stream() //
          .map(Se2GroupElement::new) //
          .map(se2GroupElement -> se2GroupElement.combine(OFS_L)));
      laneBoundaryR = Tensor.of(optional.get().stream() //
          .map(Se2GroupElement::new) //
          .map(se2GroupElement -> se2GroupElement.combine(OFS_R)));
    } else
      System.err.println("Curve empty.");
  }

  final Optional<Tensor> getCurve() {
    System.out.println("got curve");
    return optionalCurve;
  }

  /** @param curve
   * @param pose
   * @return clip values with unit "SCE" */
  Optional<Clip> getPermittedRange(Tensor curve, Tensor pose) {
    // TODO AM/JPH perhaps choose different default/fallback values:
    Scalar steerlimitL_SCE = Quantity.of(0, "SCE");
    Scalar steerlimitR_SCE = Quantity.of(0, "SCE");
    if (1 < curve.length()) {
      if (HapticSteerConfig.GLOBAL.printLaneInfo)
        System.out.println("ifloop entered :)");
      Optional<ClothoidPlan> optionalL = //
          curvePlannerL.getPlan(pose, velocity.Get(0), laneBoundaryL, true);
      Optional<ClothoidPlan> optionalR = //
          curvePlannerR.getPlan(pose, velocity.Get(0), laneBoundaryR, true);
      if (HapticSteerConfig.GLOBAL.printLaneInfo)
        System.out.println(optionalL);
      if (optionalL.isPresent()) {
        Scalar steerlimitLratio = steerConfig.getRatioLimit().apply(optionalL.get().ratio());
        steerlimitL_SCE = steerMapping.getSCEfromRatio(steerlimitLratio);
        if (HapticSteerConfig.GLOBAL.printLaneInfo)
          System.out.println("Limit L: " + steerlimitL_SCE);
      }
      if (optionalR.isPresent()) {
        Scalar steerlimitRratio = steerConfig.getRatioLimit().apply(optionalR.get().ratio());
        steerlimitR_SCE = steerMapping.getSCEfromRatio(steerlimitRratio);
        if (HapticSteerConfig.GLOBAL.printLaneInfo)
          System.out.println("Limit R: " + steerlimitR_SCE);
      }
      if (optionalL.isPresent() && optionalR.isPresent()) {
        try {
          return Optional.of(Clips.interval(steerlimitR_SCE, steerlimitL_SCE));
        } catch (Exception e) {
          System.out.println("bad clip");
        }
      }
    }
    if (HapticSteerConfig.GLOBAL.printLaneInfo) {
      System.out.println("no steer limit found");
      System.out.println("ifloop not entered :(");
    }
    return Optional.empty();
  }

  @Override // from TensorListener
  public void tensorReceived(Tensor tensor) {
    setCurve(tensor.length() <= 1 //
        ? Optional.empty()
        : Optional.of(tensor));
  }
}
