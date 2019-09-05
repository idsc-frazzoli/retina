// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.io.File;
import java.util.Date;
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
import ch.ethz.idsc.retina.util.time.SystemTimestamp;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.TensorListener;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/**  */
public class LaneKeepingCenterlineModule extends AbstractClockedModule implements //
    GokartPoseListener, TensorListener {
  // ---
  private final CurveSe2PursuitLcmClient curveSe2PursuitLcmClient = new CurveSe2PursuitLcmClient();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final CurveClothoidPursuitPlanner curvePlannerL;
  private final CurveClothoidPursuitPlanner curvePlannerR;
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
    return HapticSteerConfig.GLOBAL.laneKeepingPeriod;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  synchronized void setCurve(Optional<Tensor> optional) {
    optionalCurve = optional;
    LaneKeepingCenterlineModule.exportTensor(optionalCurve.get());
    if (optional.isPresent()) {
      // TODO JPH OWL 053 use StableLane (without control points!)
      Tensor OFS_L = Tensors.of(Quantity.of(0, SI.METER), HapticSteerConfig.GLOBAL.halfWidth, RealScalar.ZERO).unmodifiable();
      Tensor OFS_R = Tensors.of(Quantity.of(0, SI.METER), HapticSteerConfig.GLOBAL.halfWidth.negate(), RealScalar.ZERO).unmodifiable();
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

  protected static void exportTensor(Tensor tensor) {
    File file = HomeDirectory.Desktop("setCurveRefined_" + SystemTimestamp.asString(new Date()) + ".csv");
    try {
      Put.of(file, tensor);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /** @param curve
   * @param pose
   * @return clip values with unit "SCE" */
  Optional<Clip> getPermittedRange(Tensor curve, Tensor pose) {
    if (1 < curve.length()) {
      Optional<ClothoidPlan> optionalL = curvePlannerL.getPlan(pose, velocity, laneBoundaryL, true);
      Optional<ClothoidPlan> optionalR = curvePlannerR.getPlan(pose, velocity, laneBoundaryR, true);
      if (optionalL.isPresent() && //
          optionalR.isPresent()) {
        Scalar steerlimitL_SCE = steerMapping.getSCEfromRatio(optionalL.get().ratio());
        Scalar steerlimitR_SCE = steerMapping.getSCEfromRatio(optionalR.get().ratio());
        try {
          return Optional.of(Clips.interval(steerlimitR_SCE, steerlimitL_SCE));
        } catch (Exception exception) {
          System.err.println("bad clip " + steerlimitR_SCE + " " + steerlimitL_SCE);
        }
      }
    }
    System.err.println("no steer limit found");
    return Optional.empty();
  }

  @Override // from TensorListener
  public void tensorReceived(Tensor tensor) {
    setCurve(tensor.length() <= 1 //
        ? Optional.empty()
        : Optional.of(tensor));
  }
}
