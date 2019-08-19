// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

/** class is the default choice for pursuit when driving along a curve in global
 * coordinates while the pose is updated periodically from a localization method. */
public abstract class CurvePursuitModule extends PursuitModule implements GokartPoseListener {
  private final Clip ratioClip = SteerConfig.GLOBAL.getRatioLimit();
  private final Chop speedChop = RimoConfig.GLOBAL.speedChop();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  // ---
  protected Optional<Tensor> optionalCurve = Optional.empty();
  protected boolean closed = true;
  GokartPoseEvent gokartPoseEvent = null;

  public CurvePursuitModule(PursuitConfig pursuitConfig) {
    super(pursuitConfig);
  }

  @Override // from PursuitModule
  protected final void protected_first() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
  }

  @Override // from PursuitModule
  protected void protected_last() {
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from PursuitModule
  protected final Optional<Scalar> deriveHeading() {
    GokartPoseEvent gokartPoseEvent = this.gokartPoseEvent; // copy reference instead of synchronize
    // System.err.println("check isOperational");
    if (Objects.nonNull(gokartPoseEvent)) { // is localization pose available?
      Tensor pose = gokartPoseEvent.getPose(); // latest pose
      Optional<Scalar> optional = getRatio(pose);
      if (optional.isPresent()) { // is look ahead beacon available?
        Scalar ratio = optional.get(); // ChassisGeometry.GLOBAL.steerAngleForTurningRatio();
        if (ratioClip.isInside(ratio)) // is look ahead beacon within steering range?
          return Optional.of(ratio);
        System.err.println("beacon outside steering range");
      }
    }
    return Optional.empty(); // autonomous operation denied
  }

  /** @param pose of vehicle {x[m], y[m], angle}
   * @return ratio rate [m^-1] */
  protected abstract Optional<Scalar> getRatio(Tensor pose);

  /** @param curve world frame coordinates */
  public synchronized final void setCurve(Optional<Tensor> curve) {
    setCurve(curve, true);
  }

  public synchronized final void setCurve(Optional<Tensor> curve, boolean closed) {
    optionalCurve = curve;
    this.closed = closed;
  }

  public synchronized final void setTrajectory(List<TrajectorySample> trajectory) {
    setTrajectory(trajectory, false);
  }

  public synchronized final void setTrajectory(List<TrajectorySample> trajectory, boolean closed) {
    Tensor curve = Tensor.of(trajectory.stream() //
        .map(TrajectorySample::stateTime) //
        .map(StateTime::state) //
        .map(row -> row.extract(0, 3)) //
        .map(PoseHelper::attachUnits));
    setCurve(Optional.of(curve), closed);
  }

  @Override // from GokartPoseListener
  public final void getEvent(GokartPoseEvent gokartPoseEvent) { // arrives at 20[Hz]
    this.gokartPoseEvent = gokartPoseEvent;
  }

  /***************************************************/
  /** @return curve world frame coordinates */
  public final Optional<Tensor> getCurve() {
    return optionalCurve;
  }

  /** @return true if gokart is stationary or moving forwards */
  /* package */ final boolean isForward() {
    return Sign.isPositiveOrZero(speedChop.apply(gokartPoseEvent.getVelocity().Get(0)));
  }
}
