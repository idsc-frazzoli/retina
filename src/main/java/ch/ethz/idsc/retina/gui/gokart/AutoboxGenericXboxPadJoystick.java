// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owly.data.TimeKeeper;
import ch.ethz.idsc.owly.demo.rice.Rice1StateSpaceModel;
import ch.ethz.idsc.owly.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owly.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.StateTime;
import ch.ethz.idsc.retina.dev.joystick.GenericXboxPadJoystick;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutConfiguration;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetListener;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.zhkart.DriveMode;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

public class AutoboxGenericXboxPadJoystick implements JoystickListener, SteerGetListener {
  /** no joystick info older than watchdog period is used */
  private static final int WATCHDOG_MS = 500; // 500[ms]
  // ---
  private final TimeKeeper timeKeeper = new TimeKeeper();
  private GenericXboxPadJoystick _joystick;
  private long tic_joystick;
  public int speedLimit = 1000;
  // TODO NRJ test simple drive
  public DriveMode driveMode = DriveMode.FULL_CONTROL;
  private SteerGetEvent steerGetEvent;
  private final DifferentialSpeed dsL = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(+0.54));
  private final DifferentialSpeed dsR = new DifferentialSpeed(RealScalar.of(1.2), RealScalar.of(-0.54));
  private final EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
      new Rice1StateSpaceModel(RealScalar.of(1)), //
      MidpointIntegrator.INSTANCE, //
      new StateTime(Array.zeros(1), RealScalar.ZERO));

  @Override
  public void joystick(JoystickEvent joystickEvent) {
    _joystick = (GenericXboxPadJoystick) joystickEvent;
    tic_joystick = now();
  }

  private boolean hasJoystick() {
    return Objects.nonNull(steerGetEvent) //
        && Objects.nonNull(_joystick) //
        && now() < tic_joystick + WATCHDOG_MS;
  }

  /** steering */
  public final SteerPutProvider steerPutProvider = new SteerPutProvider() {
    @SuppressWarnings("incomplete-switch")
    @Override
    public Optional<SteerPutEvent> getPutEvent() {
      if (hasJoystick()) {
        GenericXboxPadJoystick joystick = _joystick;
        double value = 0;
        switch (driveMode) {
        case SIMPLE_DRIVE: {
          value = -joystick.getRightKnobDirectionRight();
          break;
        }
        case FULL_CONTROL: {
          if (joystick.isButtonPressedB())
            value = 1;
          if (joystick.isButtonPressedX())
            value = -1;
          break;
        }
        }
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

    @SuppressWarnings("incomplete-switch")
    @Override
    public Optional<RimoPutEvent> getPutEvent() {
      final Scalar now = timeKeeper.now();
      Scalar push = RealScalar.ZERO;
      if (hasJoystick())
        push = RealScalar.of(_joystick.getRightKnobDirectionUp() * speedLimit);
      episodeIntegrator.move(Tensors.of(push), now);
      if (hasJoystick()) {
        GenericXboxPadJoystick joystick = _joystick;
        switch (driveMode) {
        case SIMPLE_DRIVE: {
          StateTime rate = episodeIntegrator.tail();
          Scalar speed = rate.state().Get(0);
          Scalar theta = RealScalar.of(steerGetEvent.getSteeringAngle());
          Scalar sL = dsL.get(speed, theta);
          Scalar sR = dsR.get(speed, theta);
          return Optional.of(RimoPutEvent.withSpeeds( //
              sL.number().shortValue(), //
              sR.number().shortValue()));
        }
        case FULL_CONTROL: {
          if (joystick.isButtonPressedBack())
            sign = -1;
          if (joystick.isButtonPressedStart())
            sign = 1;
          double wheelL = joystick.getLeftSliderUnitValue();
          double wheelR = joystick.getRightSliderUnitValue();
          return Optional.of(RimoPutEvent.withSpeeds( //
              (short) (wheelL * speedLimit * sign), //
              (short) (wheelR * speedLimit * sign)));
        }
        }
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
        GenericXboxPadJoystick joystick = _joystick;
        double value = joystick.getLeftKnobDirectionDown();
        int pos = (int) //
        Math.min(Math.max(LinmotPutConfiguration.TARGETPOS_MIN, //
            (LinmotPutConfiguration.TARGETPOS_MIN * value + LinmotPutConfiguration.TARGETPOS_INIT)), //
            LinmotPutConfiguration.TARGETPOS_MAX);
        LinmotPutEvent linmotPutEvent = LinmotPutEvent.NORMAL_MODE;
        // TODO NRJ check values
        linmotPutEvent.target_position = (short) pos;
        linmotPutEvent.max_velocity = 1000;
        linmotPutEvent.acceleration = 500;
        linmotPutEvent.deceleration = 500;
        return Optional.of(linmotPutEvent);
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.MANUAL;
    }
  };

  private static long now() {
    return System.currentTimeMillis();
  }

  @Override
  public void getEvent(SteerGetEvent getEvent) {
    steerGetEvent = getEvent;
  }
}
