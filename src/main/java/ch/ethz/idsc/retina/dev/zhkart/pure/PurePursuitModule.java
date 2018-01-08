// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.se2.glc.PurePursuit;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseEvent;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseLcmClient;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseListener;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Clip;

public class PurePursuitModule extends AbstractClockedModule implements GokartPoseListener {
  public static final Tensor CURVE = DubendorfCurve.OVAL;
  public static final Clip VALID_RANGE = SteerConfig.GLOBAL.getAngleLimit();
  // ---
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  final PurePursuitSteer purePursuitSteer = new PurePursuitSteer();
  final PurePursuitRimo purePursuitRimo = new PurePursuitRimo();
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);
  // ---
  private GokartPoseEvent gokartPoseEvent = null;

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
    if (Objects.nonNull(gokartPoseEvent)) {
      boolean status = true;
      // ---
      final float quality = gokartPoseEvent.getQuality();
      System.out.println("q=" + quality);
      status &= 0.10 < quality || quality == 0; // TODO magic const FIXME hack
      if (!status)
        System.err.println("quality insufficient");
      // ---
      Tensor pose = gokartPoseEvent.getPose(); // latest pose
      Optional<Scalar> optional = getLookAhead(pose, CURVE);
      status &= optional.isPresent();
      if (status) {
        Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(optional.get());
        status = VALID_RANGE.isInside(angle);
        if (status)
          purePursuitSteer.setHeading(angle);
        else
          System.err.println("invalid range");
      } else
        System.err.println("look ahead off");
      Optional<JoystickEvent> joystick = joystickLcmClient.getJoystick();
      if (joystick.isPresent()) {
        GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystick.get();
        status &= gokartJoystickInterface.isAutonomousPressed();
      } else
        status = false;
      purePursuitSteer.setOperational(status);
      purePursuitRimo.setOperational(status);
    }
  }

  /* package */ static Optional<Scalar> getLookAhead(Tensor pose, Tensor curve) {
    Tensor poseNoUnits = pose.map(scalar -> RealScalar.of(scalar.number()));
    TensorUnaryOperator tensorUnaryOperator = new Se2Bijection(poseNoUnits).inverse();
    Tensor tensor = Tensor.of(curve.stream().map(tensorUnaryOperator));
    Scalar distance = PursuitConfig.GLOBAL.lookAheadMeter();
    Optional<Tensor> aheadTrail = CurveUtils.getAheadTrail(tensor, distance);
    if (aheadTrail.isPresent())
      return PurePursuit.turningRatePositiveX(aheadTrail.get(), distance);
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
