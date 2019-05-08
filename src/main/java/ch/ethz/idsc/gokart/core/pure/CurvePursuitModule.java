// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoConfig;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;

/** class is the default choice for pure pursuit when driving along a curve in global
 * coordinates while the pose is updated periodically from a localization method. */
public abstract class CurvePursuitModule extends PurePursuitModule implements GokartPoseListener {
  private final Chop speedChop = RimoConfig.GLOBAL.speedChop();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  /** forward motion is determined by odometry:
   * noise in the measurements around zero are also mapped to "forward" */
  protected boolean isForward = true;
  protected Scalar speed = Quantity.of(0, SI.VELOCITY);
  /* package */ final RimoGetListener rimoGetListener = new RimoGetListener() {
    @Override
    public void getEvent(RimoGetEvent rimoGetEvent) {
      speed = speedChop.apply(ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent));
      isForward = Sign.isPositiveOrZero(speed);
    }
  };
  // ---
  protected Optional<Tensor> optionalCurve = Optional.empty();
  protected boolean closed = true;
  GokartPoseEvent gokartPoseEvent = null;

  public CurvePursuitModule(PursuitConfig pursuitConfig) {
    super(pursuitConfig);
  }

  @Override // from PurePursuitModule
  protected final void protected_first() {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    rimoGetLcmClient.addListener(rimoGetListener);
    rimoGetLcmClient.startSubscriptions();
  }

  @Override // from PurePursuitModule
  protected void protected_last() {
    rimoGetLcmClient.stopSubscriptions();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from PurePursuitModule
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

  @Override // from GokartPoseListener
  public final void getEvent(GokartPoseEvent gokartPoseEvent) { // arrives at 20[Hz]
    this.gokartPoseEvent = gokartPoseEvent;
  }

  /***************************************************/
  /** @return curve world frame coordinates */
  /* package */ final Optional<Tensor> getCurve() {
    return optionalCurve;
  }

  /** @return true if gokart is stationary or moving forwards */
  /* package */ final boolean isForward() {
    return isForward;
  }
}
