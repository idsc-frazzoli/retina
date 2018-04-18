// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.owl.math.planar.PurePursuit;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Ramp;

// TODO pose quality check could be an independent fuse module preventing autonomous modes
public class PurePursuitModule extends AbstractClockedModule implements GokartPoseListener {
  /** until 20180226 the curve for trajectory pursuit was
   * DubendorfCurve.OVAL
   * 
   * due to new safety structure, the curve made a bit smaller and shifted slightly
   * in the direction away from the container. the new curve is
   * DubendorfCurve.OVAL_SHIFTED
   * 
   * both trajectories are in clockwise direction */
  public static final Tensor CURVE = DubendorfCurve.EIGHT_HYPERLOOP;
  public static final Clip VALID_RANGE = SteerConfig.GLOBAL.getAngleLimit();
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  final PurePursuitSteer purePursuitSteer = new PurePursuitSteer();
  final PurePursuitRimo purePursuitRimo = new PurePursuitRimo();
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
  private Tensor curve = CURVE;
  // ---
  private GokartPoseEvent gokartPoseEvent = null;

  /** function setCurve is for testing only.
   * for normal operation, set the curve via the static field CURVE
   * 
   * @param curve */
  void setCurve(Tensor curve) {
    this.curve = curve;
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    joystickLcmClient.startSubscriptions();
    purePursuitRimo.start();
    purePursuitSteer.start();
  }

  @Override // from AbstractModule
  protected void last() {
    purePursuitRimo.stop();
    purePursuitSteer.stop();
    gokartPoseLcmClient.stopSubscriptions();
    joystickLcmClient.stopSubscriptions();
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    boolean status = isOperational();
    purePursuitSteer.setOperational(status);
    Optional<JoystickEvent> joystick = joystickLcmClient.getJoystick();
    if (joystick.isPresent()) { // is joystick button "autonomous" pressed?
      GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystick.get();
      Scalar ratio = Ramp.FUNCTION.apply(gokartJoystickInterface.getAheadAverage());
      purePursuitRimo.setSpeed(PursuitConfig.GLOBAL.rateFollower.multiply(ratio));
    }
    purePursuitRimo.setOperational(status);
  }

  private boolean isOperational() {
    if (Objects.nonNull(gokartPoseEvent)) { // is localization pose available?
      final Scalar quality = gokartPoseEvent.getQuality();
      if (PursuitConfig.GLOBAL.isQualitySufficient(quality)) { // is localization quality sufficient?
        Tensor pose = gokartPoseEvent.getPose(); // latest pose
        Optional<Scalar> optional = getLookAhead(pose, curve);
        if (optional.isPresent()) { // is look ahead beacon available?
          Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(optional.get());
          if (VALID_RANGE.isInside(angle)) { // is look ahead beacon within steering range?
            purePursuitSteer.setHeading(angle);
            Optional<JoystickEvent> joystick = joystickLcmClient.getJoystick();
            if (joystick.isPresent()) { // is joystick button "autonomous" pressed?
              GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystick.get();
              return gokartJoystickInterface.isAutonomousPressed();
            }
          }
        }
      }
    }
    return false; // autonomous operation denied
  }

  /* package */ static Optional<Scalar> getLookAhead(Tensor pose, Tensor curve) {
    Tensor poseNoUnits = pose.map(scalar -> RealScalar.of(scalar.number()));
    TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(poseNoUnits).inverse();
    Tensor tensor = Tensor.of(curve.stream().map(tensorUnaryOperator));
    Scalar distance = PursuitConfig.GLOBAL.lookAheadMeter();
    Optional<Tensor> aheadTrail = CurveUtils.getAheadTrail(tensor, distance);
    if (aheadTrail.isPresent()) {
      PurePursuit purePursuit = PurePursuit.fromTrajectory(aheadTrail.get(), distance);
      return purePursuit.ratio();
    }
    return Optional.empty();
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return PursuitConfig.GLOBAL.updatePeriod;
  }

  @Override // from GokartPoseListener
  public void getEvent(GokartPoseEvent gokartPoseEvent) { // arrives at 50[Hz]
    this.gokartPoseEvent = gokartPoseEvent;
  }
}
