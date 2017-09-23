// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GenericXboxPadJoystick;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutConfiguration;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

public enum AutoboxGenericXboxPadJoystick implements JoystickListener {
  INSTANCE;
  // ---
  private GenericXboxPadJoystick _genericXboxPadJoystick;
  private long lastUpdate;
  private int speedlimitjoystick = 1000;

  @Override
  public void joystick(JoystickEvent joystickEvent) {
    _genericXboxPadJoystick = (GenericXboxPadJoystick) joystickEvent;
    lastUpdate = System.currentTimeMillis();
  }

  private boolean hasJoystick() {
    return Objects.nonNull(_genericXboxPadJoystick) && System.currentTimeMillis() < lastUpdate + 1000;
  }

  /** steering */
  public final SteerPutProvider steerPutProvider = new SteerPutProvider() {
    @Override
    public Optional<SteerPutEvent> getPutEvent() {
      if (hasJoystick()) {
        double value = -_genericXboxPadJoystick.getRightKnobDirectionRight();
        return Optional.of(new SteerPutEvent(SteerPutEvent.CMD_ON, (float) value));
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.MANUAL;
    }
  };
  /** tire speed */
  public final RimoPutProvider rimoPutProvider = new RimoPutProvider() {
    int sign = 1;

    @Override
    public Optional<RimoPutEvent> getPutEvent() {
      if (hasJoystick()) {
        GenericXboxPadJoystick joystick = _genericXboxPadJoystick;
        if (joystick.isButtonPressedBack())
          sign = -1;
        if (joystick.isButtonPressedStart())
          sign = 1;
        double wheelL = joystick.getLeftSliderUnitValue();
        short sL = (short) (wheelL * speedlimitjoystick * sign);
        double wheelR = joystick.getRightSliderUnitValue();
        short sR = (short) (wheelR * speedlimitjoystick * sign);
        return Optional.of(new RimoPutEvent( //
            new RimoPutTire(RimoPutTire.OPERATION, sL), //
            new RimoPutTire(RimoPutTire.OPERATION, sR)));
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.MANUAL;
    }
  };
  /** breaking */
  public final LinmotPutProvider linmotPutProvider = new LinmotPutProvider() {
    @Override
    public Optional<LinmotPutEvent> getPutEvent() {
      if (hasJoystick()) {
        GenericXboxPadJoystick joystick = _genericXboxPadJoystick;
        double value = joystick.getLeftKnobDirectionDown();
        int pos = (int) //
        Math.min(Math.max(LinmotPutConfiguration.TARGETPOS_MIN, //
            (LinmotPutConfiguration.TARGETPOS_MIN * value + LinmotPutConfiguration.TARGETPOS_INIT)), //
            LinmotPutConfiguration.TARGETPOS_MAX);
        // TODO this is redundant!
        LinmotPutEvent linmotPutEvent = new LinmotPutEvent(LinmotPutConfiguration.CMD_OPERATION, //
            LinmotPutConfiguration.MC_POSITION);
        linmotPutEvent.target_position = (short) pos;
        // TODO NRJ assign others
        return Optional.of(linmotPutEvent);
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.MANUAL;
    }
  };
  // if (isJoystickEnabled()) {
  // GenericXboxPadJoystick joystick = (GenericXboxPadJoystick) joystickEvent;
  // }
  // final Scalar now = timeKeeper.now();
  // Scalar push = RealScalar.ZERO;
  // if (isJoystickEnabled()) {
  // GenericXboxPadJoystick joystick = (GenericXboxPadJoystick) joystickEvent;
  // push = RealScalar.of(joystick.getRightKnobDirectionUp() * speedlimitjoystick);
  // }
  // episodeIntegrator.move(Tensors.of(push), now);
  // // ---
  // if (isJoystickEnabled()) {
  // GenericXboxPadJoystick joystick = (GenericXboxPadJoystick) joystickEvent;
  // switch (driveMode) {
  // case SIMPLE_DRIVE: {
  // final StateTime rate = episodeIntegrator.tail();
  // final Scalar speed = rate.state().Get(0);
  // final Scalar theta = Objects.isNull(lastSteer) ? RealScalar.ZERO : RealScalar.of(lastSteer.getSteeringAngle());
  // sliderExtLVel.jSlider.setValue(dsL.get(speed, theta).number().intValue());
  // sliderExtRVel.jSlider.setValue(dsR.get(speed, theta).number().intValue());
  // break;
  // }
  // case FULL_CONTROL: {
  // break;
  // }
  // default:
  // break;
  // }
  // }
  // private SteerGetEvent lastSteer = null;
  // private final DifferentialSpeed dsL = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(+0.54));
  // private final DifferentialSpeed dsR = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(-0.54));
  // private int sign = 1;
  // public int speedlimitjoystick = 1000;
  // public DriveMode driveMode = DriveMode.SIMPLE_DRIVE;
  // private final EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
  // new Rice1StateSpaceModel(RealScalar.of(1)), //
  // MidpointIntegrator.INSTANCE, //
  // new StateTime(Array.zeros(1), RealScalar.ZERO));
  // private final TimeKeeper timeKeeper = new TimeKeeper();
  //
  // public void setspeedlimit(int i) {
  // speedlimitjoystick = i;
  // }
  //
  // public void setdrivemode(DriveMode i) {
  // driveMode = i;
  // }
  //

  public void setspeedlimit(int i) {
    speedlimitjoystick = i;
  }
}
