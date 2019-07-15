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
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.TensorListener;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/**  */
public class LaneKeepingCenterlineModule extends AbstractClockedModule implements GokartPoseListener, TensorListener {
  private final CurveSe2PursuitLcmClient curveSe2PursuitLcmClient = new CurveSe2PursuitLcmClient();
  public GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private static final Scalar PERIOD = Quantity.of(0.1, SI.SECOND);
  public GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  public Optional<Tensor> optionalCurve = Optional.empty();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  public Optional<Clip> optionalPermittedRange;
  public Tensor velocity;

  @Override
  protected void runAlgo() {
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
    if (isPresent && isQualityOk) {
      optionalPermittedRange = getPermittedRange(curve, pose);
      System.out.println(optionalPermittedRange);
    }
  }

  @Override
  protected Scalar getPeriod() {
    return PERIOD;
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

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  public void setCurve(Optional<Tensor> curve) {
    if (curve.isPresent()) {
      optionalCurve = curve;
    } else {
      System.err.println("Curve missing");
      optionalCurve = Optional.empty();
    }
  }

  final Optional<Tensor> getCurve() {
    System.out.println("got curve");
    return optionalCurve;
  }

  public Optional<Clip> getPermittedRange(Tensor curve, Tensor pose) {
    Scalar steerlimitLSCE = Quantity.of(0, "SCE");
    Scalar steerlimitRSCE = Quantity.of(0, "SCE");
    if (1 < curve.length()) {
      System.out.println("ifloop entered :)");
      ClothoidPursuitConfig clothoidPursuitConfig = new ClothoidPursuitConfig();
      // large value is a hack to get a solution
      clothoidPursuitConfig.turningRatioMax = Quantity.of(1000, SI.PER_METER);
      Optional<ClothoidPlan> optionalL = //
          new CurveClothoidPursuitPlanner(clothoidPursuitConfig).getPlan(pose, Quantity.of(0, SI.VELOCITY), curve.get(), true);
      Optional<ClothoidPlan> optionalR = //
          new CurveClothoidPursuitPlanner(clothoidPursuitConfig).getPlan(pose, Quantity.of(0, SI.VELOCITY), curve.get(), true);
      System.out.println(optionalL);
      if (optionalL.isPresent()) {
        Scalar steerlimitLratio = optionalL.get().ratio();
        steerlimitLSCE = steerMapping.getSCEfromRatio(steerlimitLratio);
        System.out.println("Limit L: " + steerlimitLSCE);
      }
      if (optionalR.isPresent()) {
        Scalar steerlimitRratio = optionalR.get().ratio();
        steerlimitRSCE = steerMapping.getSCEfromRatio(steerlimitRratio);
        System.out.println("Limit R: " + steerlimitRSCE);
      }
      return Optional.of(Clips.interval(steerlimitRSCE, steerlimitLSCE));
    }
    System.out.println("no steer limit found");
    System.out.println("ifloop not entered :(");
    return Optional.empty();
  }

  @Override
  public void tensorReceived(Tensor tensor) {
    this.setCurve(Optional.of(tensor));
  }
}
