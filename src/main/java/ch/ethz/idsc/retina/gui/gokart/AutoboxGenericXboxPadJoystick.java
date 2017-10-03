// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.owly.data.TimeKeeper;
import ch.ethz.idsc.owly.demo.rice.Rice1StateSpaceModel;
import ch.ethz.idsc.owly.math.car.DifferentialSpeed;
import ch.ethz.idsc.owly.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owly.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.StateTime;
import ch.ethz.idsc.retina.dev.joystick.GenericXboxPadJoystick;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.retina.dev.misc.MiscPutEvent;
import ch.ethz.idsc.retina.dev.misc.MiscPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.steer.PDSteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.DriveMode;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO JAN breakup joystick modes into two classes
public class AutoboxGenericXboxPadJoystick implements JoystickListener {
  /** no joystick info older than watchdog period is used */
  private static final int WATCHDOG_MS = 500; // 500[ms]
  private static final Scalar AXIS_DELTA = Quantity.of(1.2, "m");
  private static final Scalar TIRE_L = Quantity.of(+0.54, "m");
  private static final Scalar TIRE_R = Quantity.of(-0.54, "m");
  // ---
  private final TimeKeeper timeKeeper = new TimeKeeper();
  private GenericXboxPadJoystick _joystick;
  private long tic_joystick;
  public int speedLimit = 1000;
  public DriveMode driveMode = DriveMode.FULL_CONTROL;
  private final PDSteerPositionControl positionController = new PDSteerPositionControl();
  private final DifferentialSpeed dsL = new DifferentialSpeed(AXIS_DELTA, TIRE_L);
  private final DifferentialSpeed dsR = new DifferentialSpeed(AXIS_DELTA, TIRE_R);
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
    return // Objects.nonNull(steerGetEvent) //
    // &&
    Objects.nonNull(_joystick) //
        && now() < tic_joystick + WATCHDOG_MS;
  }

  /** steering */
  public final SteerPutProvider steerPutProvider = new SteerPutProvider() {
    @SuppressWarnings("incomplete-switch")
    @Override
    public Optional<SteerPutEvent> putEvent() {
      if (hasJoystick())
        if (SteerSocket.INSTANCE.getSteerAngleTracker().isCalibrated()) {
          GenericXboxPadJoystick joystick = _joystick;
          Scalar value = RealScalar.ZERO;
          switch (driveMode) {
          case SIMPLE_DRIVE: {
            final double currAngle = SteerSocket.INSTANCE.getSteerAngleTracker().getValueWithOffset();
            double desPos = -joystick.getRightKnobDirectionRight() * SteerPutEvent.MAX_ANGLE;
            double errPos = desPos - currAngle;
            final double torqueCmd = positionController.iterate(errPos);
            value = RealScalar.of(torqueCmd).multiply(torqueAmp);
            break;
          }
          case FULL_CONTROL: {
            if (joystick.isButtonPressedB())
              value = torqueAmp;
            if (joystick.isButtonPressedX())
              value = torqueAmp.negate();
            break;
          }
          }
          return Optional.of(new SteerPutEvent(SteerPutEvent.CMD_ON, value.number().doubleValue()));
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
    public Optional<RimoPutEvent> putEvent() {
      final Scalar now = timeKeeper.now();
      Scalar push = RealScalar.ZERO;
      if (hasJoystick())
        push = RealScalar.of(_joystick.getRightKnobDirectionUp() * speedLimit);
      episodeIntegrator.move(Tensors.of(push), now);
      if (hasJoystick()) {
        GenericXboxPadJoystick joystick = _joystick;
        switch (driveMode) {
        case SIMPLE_DRIVE: {
          if (SteerSocket.INSTANCE.getSteerAngleTracker().isCalibrated()) {
            StateTime rate = episodeIntegrator.tail();
            Scalar speed = rate.state().Get(0);
            Scalar theta = RealScalar.of(SteerSocket.INSTANCE.getSteerAngleTracker().getSteeringValue());
            Scalar sL = dsL.get(speed, theta);
            Scalar sR = dsR.get(speed, theta);
            return Optional.of(RimoPutEvent.withSpeeds( //
                sL.number().shortValue(), //
                sR.number().shortValue()));
          }
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
    public Optional<LinmotPutEvent> putEvent() {
      if (hasJoystick()) {
        GenericXboxPadJoystick joystick = _joystick;
        double value = joystick.getLeftKnobDirectionDown();
        return Optional.of(LinmotPutHelper.operationToRelativePosition(value));
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.MANUAL;
    }
  };
  /** reset Misc **/
  public final MiscPutProvider miscPutProvider = new MiscPutProvider() {
    @Override
    public Optional<MiscPutEvent> putEvent() {
      if (hasJoystick()) {
        byte resetValue = (byte) (_joystick.isButtonPressedBlack() ? 1 : 0);
        MiscPutEvent miscPutEvent = new MiscPutEvent();
        miscPutEvent.resetRimoL = resetValue;
        miscPutEvent.resetRimoR = resetValue;
        // TODO NRJ not final logic
        return Optional.of(miscPutEvent);
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.MANUAL;
    }
  };
  public Scalar torqueAmp = RationalScalar.of(1, 2);

  private static long now() {
    return System.currentTimeMillis();
  }
}
