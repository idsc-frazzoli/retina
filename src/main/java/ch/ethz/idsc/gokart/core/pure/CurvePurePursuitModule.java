// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.retina.dev.rimo.RimoConfig;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;

public final class CurvePurePursuitModule extends PurePursuitModule implements GokartPoseListener {
  private Optional<Tensor> optionalCurve = Optional.empty();
  private final Chop speedChop = RimoConfig.GLOBAL.speedChop();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private GokartPoseEvent gokartPoseEvent = null;
  /** forward motion is determined by odometry:
   * noise in the measurements around zero are also mapped to "forward" */
  private boolean isForward = true;
  /* package */ final RimoGetListener rimoGetListener = new RimoGetListener() {
    @Override
    public void getEvent(RimoGetEvent rimoGetEvent) {
      Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent);
      isForward = Sign.isPositiveOrZero(speedChop.apply(speed));
    }
  };

  @Override // from AbstractModule
  protected void protected_first() throws Exception {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addGetListener(rimoGetListener);
  }

  @Override // from AbstractModule
  protected void protected_last() {
    RimoSocket.INSTANCE.removeGetListener(rimoGetListener);
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from PurePursuitModule
  protected Optional<Scalar> deriveHeading() {
    GokartPoseEvent gokartPoseEvent = this.gokartPoseEvent; // copy reference instead of synchronize
    // System.err.println("check isOperational");
    if (Objects.nonNull(gokartPoseEvent)) { // is localization pose available?
      final Scalar quality = gokartPoseEvent.getQuality();
      if (PursuitConfig.GLOBAL.isQualitySufficient(quality)) { // is localization quality sufficient?
        Tensor pose = gokartPoseEvent.getPose(); // latest pose
        Optional<Scalar> ratio = getRatio(pose);
        if (ratio.isPresent()) { // is look ahead beacon available?
          Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(ratio.get());
          if (angleClip.isInside(angle)) // is look ahead beacon within steering range?
            return Optional.of(angle);
          System.err.println("beacon outside steering range");
        }
      } else
        System.err.println("pose quality insufficient");
    }
    return Optional.empty(); // autonomous operation denied
  }

  // TODO JPH function should return a scalar with unit "rad*m^-1"...
  // right now, "curve" does not have "m" as unit but entries are unitless.
  /** @param pose
   * @return */
  /* package */ Optional<Scalar> getRatio(Tensor pose) {
    Optional<Tensor> optionalCurve = this.optionalCurve; // copy reference instead of synchronize
    if (optionalCurve.isPresent())
      return getRatio(pose, optionalCurve.get(), isForward);
    System.err.println("no curve in pure pursuit");
    return Optional.empty();
  }

  static Optional<Scalar> getRatio(Tensor pose, Tensor curve, boolean isForward) {
    TensorUnaryOperator toLocal = new Se2Bijection(GokartPoseHelper.toUnitless(pose)).inverse();
    Tensor tensor = Tensor.of(curve.stream().map(toLocal));
    if (!isForward) { // if measured tangent speed is negative
      tensor.set(Scalar::negate, Tensor.ALL, 0); // flip sign of X coord. of waypoints in tensor
      tensor = Reverse.of(tensor); // reverse order of points along trajectory
    }
    Scalar distance = PursuitConfig.GLOBAL.lookAheadMeter();
    Optional<Tensor> aheadTrail = CurveUtils.getAheadTrail(tensor, distance);
    if (aheadTrail.isPresent()) {
      PurePursuit purePursuit = PurePursuit.fromTrajectory(aheadTrail.get(), distance);
      return purePursuit.ratio();
    }
    return Optional.empty();
  }

  /** @param curve */
  public void setCurve(Optional<Tensor> curve) {
    optionalCurve = curve;
  }

  /** @return curve */
  /* package */ Optional<Tensor> getCurve() {
    return optionalCurve;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) { // arrives at 20[Hz]
    this.gokartPoseEvent = gokartPoseEvent;
  }

  public boolean isForward() {
    return isForward;
  }
}
