// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoConfig;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;

public abstract class PurePursuitModule extends AbstractClockedModule implements GokartPoseListener {
  private final Chop chop = RimoConfig.GLOBAL.speedChop();
  private final Clip angleClip = SteerConfig.GLOBAL.getAngleLimit();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  final PurePursuitSteer purePursuitSteer = new PurePursuitSteer();
  final PurePursuitRimo purePursuitRimo = new PurePursuitRimo();
  private final JoystickLcmProvider joystickLcmProvider = JoystickConfig.GLOBAL.createProvider();
  private GokartPoseEvent gokartPoseEvent = null;
  /** forward motion is determined by odometry:
   * noise in the measurements around zero are also mapped to "forward" */
  private boolean isForward = true;
  /* package */ final RimoGetListener rimoGetListener = new RimoGetListener() {
    @Override
    public void getEvent(RimoGetEvent rimoGetEvent) {
      Scalar speed = ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent);
      isForward = Sign.isPositiveOrZero(chop.apply(speed));
    }
  };

  @Override // from AbstractModule
  protected final void first() throws Exception {
    gokartPoseLcmClient.addListener(this);
    gokartPoseLcmClient.startSubscriptions();
    joystickLcmProvider.startSubscriptions();
    RimoSocket.INSTANCE.addGetListener(rimoGetListener);
    purePursuitRimo.start();
    purePursuitSteer.start();
  }

  @Override // from AbstractModule
  protected final void last() {
    purePursuitRimo.stop();
    purePursuitSteer.stop();
    RimoSocket.INSTANCE.removeGetListener(rimoGetListener);
    gokartPoseLcmClient.stopSubscriptions();
    joystickLcmProvider.stopSubscriptions();
  }

  @Override // from AbstractClockedModule
  protected final void runAlgo() {
    boolean status = isOperational();
    purePursuitSteer.setOperational(status);
    Optional<JoystickEvent> joystick = joystickLcmProvider.getJoystick();
    if (joystick.isPresent()) { // is joystick button "autonomous" pressed?
      GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystick.get();
      // ante 20180604: the ahead average was used in combination with Ramp
      Scalar ratio = gokartJoystickInterface.getAheadAverage(); // in [-1, 1]
      // post 20180604: the forward command is provided by right slider
      Scalar pair = Differences.of(gokartJoystickInterface.getAheadPair_Unit()).Get(0); // in [0, 1]
      // post 20180619: allow reverse driving
      Scalar speed = Clip.absoluteOne().apply(ratio.add(pair));
      purePursuitRimo.setSpeed(PursuitConfig.GLOBAL.rateFollower.multiply(speed));
    }
    purePursuitRimo.setOperational(status);
  }

  private boolean isOperational() {
    GokartPoseEvent gokartPoseEvent = this.gokartPoseEvent; // copy reference instead of synchronize
    // System.err.println("check isOperational");
    if (Objects.nonNull(gokartPoseEvent)) { // is localization pose available?
      // Optional<Tensor> optionalCurve = this.optionalCurve; // copy reference instead of synchronize
      // if (optionalCurve.isPresent()) {
      // System.out.println("curve is present");
      final Scalar quality = gokartPoseEvent.getQuality();
      if (PursuitConfig.GLOBAL.isQualitySufficient(quality)) { // is localization quality sufficient?
        Tensor pose = gokartPoseEvent.getPose(); // latest pose
        Optional<Scalar> ratio = getRatio(pose, isForward);
        if (ratio.isPresent()) { // is look ahead beacon available?
          Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(ratio.get());
          if (angleClip.isInside(angle)) { // is look ahead beacon within steering range?
            purePursuitSteer.setHeading(angle);
            Optional<JoystickEvent> joystick = joystickLcmProvider.getJoystick();
            if (joystick.isPresent()) { // is joystick button "autonomous" pressed?
              GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystick.get();
              return gokartJoystickInterface.isAutonomousPressed();
            }
          } else
            System.err.println("beacon outside steering range");
        }
      } else
        System.err.println("pose quality insufficient");
    }
    return false; // autonomous operation denied
  }

  @Override // from AbstractClockedModule
  protected final Scalar getPeriod() {
    return PursuitConfig.GLOBAL.updatePeriod;
  }

  @Override // from GokartPoseListener
  public final void getEvent(GokartPoseEvent gokartPoseEvent) { // arrives at 20[Hz]
    this.gokartPoseEvent = gokartPoseEvent;
  }

  public final boolean isForward() {
    return isForward;
  }

  /** @param pose {[m],[m],[-]} pose with units
   * @param isForward
   * @return */
  abstract Optional<Scalar> getRatio(Tensor pose, boolean isForward);
}
