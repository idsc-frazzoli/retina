// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GenericXboxPadJoystick;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.retina.dev.misc.MiscPutEvent;
import ch.ethz.idsc.retina.dev.misc.MiscPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.steer.PDSteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerAngleTracker;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

public abstract class HmiAbstractJoystick implements JoystickListener {
  /** no joystick info older than watchdog period is used */
  private static final int WATCHDOG_MS = 500; // 500[ms]
  // ---
  private final PDSteerPositionControl positionController = new PDSteerPositionControl();
  GenericXboxPadJoystick _joystick;
  private long tic_joystick;
  private LinmotGetEvent _linmotGetEvent;
  private LinmotPutEvent _linmotPutEvent;
  private int speedLimit = 1000;

  final boolean hasJoystick() {
    return Objects.nonNull(_joystick) && now() < tic_joystick + WATCHDOG_MS;
  }

  @Override
  public final void joystick(JoystickEvent joystickEvent) {
    _joystick = (GenericXboxPadJoystick) joystickEvent;
    tic_joystick = now();
  }

  /** steering */
  public final SteerPutProvider steerPutProvider = new SteerPutProvider() {
    @Override
    public Optional<SteerPutEvent> putEvent() {
      if (hasJoystick()) {
        final SteerAngleTracker steerAngleTracker = SteerSocket.INSTANCE.getSteerAngleTracker();
        if (steerAngleTracker.isCalibrated()) {
          final double currAngle = steerAngleTracker.getSteeringValue();
          double desPos = -_joystick.getRightKnobDirectionRight() * SteerPutEvent.MAX_ANGLE;
          final double torqueCmd = positionController.iterate(desPos - currAngle);
          return Optional.of(new SteerPutEvent(SteerPutEvent.CMD_ON, torqueCmd));
        }
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
  public final LinmotGetListener linmotGetListener = new LinmotGetListener() {
    @Override
    public void getEvent(LinmotGetEvent linmotGetEvent) {
      _linmotGetEvent = linmotGetEvent;
    }
  };
  public final LinmotPutListener linmotPutListener = new LinmotPutListener() {
    @Override
    public void putEvent(LinmotPutEvent linmotPutEvent) {
      _linmotPutEvent = linmotPutEvent;
    }
  };
  /** breaking */
  public final LinmotPutProvider linmotPutProvider = new LinmotPutProvider() {
    @Override
    public Optional<LinmotPutEvent> putEvent() {
      if (hasJoystick()) {
        boolean status = true;
        status &= Objects.nonNull(_linmotGetEvent) && _linmotGetEvent.isOperational();
        status &= Objects.nonNull(_linmotPutEvent) && _linmotPutEvent.isOperational();
        if (status) {
          double value = _joystick.getLeftKnobDirectionDown();
          return Optional.of(LinmotPutHelper.operationToRelativePosition(value));
        }
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.MANUAL;
    }
  };

  public abstract RimoPutProvider getRimoPutProvider();

  public final void setSpeedLimit(int speedLimit) {
    this.speedLimit = speedLimit;
  }

  public final int getSpeedLimit() {
    return speedLimit;
  }

  private static long now() {
    return System.currentTimeMillis();
  }
}
