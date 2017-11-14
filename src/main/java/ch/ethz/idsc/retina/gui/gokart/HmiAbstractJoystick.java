// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.steer.PDSteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerAngleTracker;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public abstract class HmiAbstractJoystick implements JoystickListener {
  /** no joystick info older than watchdog period is used */
  private static final int WATCHDOG_MS = 250; // 250[ms]
  // ---
  private final PDSteerPositionControl positionController = new PDSteerPositionControl();
  GokartJoystickInterface _joystick;
  private long tic_joystick;
  private LinmotGetEvent _linmotGetEvent;
  private LinmotPutEvent _linmotPutEvent;
  private Scalar speedLimit = Quantity.of(50, "rad*s^-1"); // TODO

  final boolean hasJoystick() {
    return Objects.nonNull(_joystick) && now() < tic_joystick + WATCHDOG_MS;
  }

  @Override
  public final void joystick(JoystickEvent joystickEvent) {
    _joystick = (GokartJoystickInterface) joystickEvent;
    tic_joystick = now();
    // System.out.println("joystick recv");
  }

  /** steering */
  public final SteerPutProvider steerPutProvider = new SteerPutProvider() {
    @Override
    public Optional<SteerPutEvent> putEvent() {
      if (hasJoystick()) {
        final SteerAngleTracker steerAngleTracker = SteerSocket.INSTANCE.getSteerAngleTracker();
        if (steerAngleTracker.isCalibrated()) {
          final double currAngle = steerAngleTracker.getSteeringValue();
          double desPos = -_joystick.getRightKnobDirectionRight() * SteerAngleTracker.MAX_ANGLE;
          final Scalar torqueCmd = positionController.iterate(RealScalar.of(desPos - currAngle));
          return Optional.of(new SteerPutEvent(SteerPutEvent.CMD_ON, torqueCmd.number().doubleValue()));
        }
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
        if (status)
          return Optional.of( //
              LinmotPutHelper.operationToRelativePosition(breakStrength()));
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.MANUAL;
    }
  };

  public abstract RimoPutProvider getRimoPutProvider();

  /** @return value in the interval [0, 1]
   * 0 means no break, and 1 means all the way */
  protected abstract double breakStrength();

  public final void setSpeedLimit(Scalar speedLimit) {
    this.speedLimit = speedLimit;
  }

  public final Scalar getSpeedLimit() {
    return speedLimit;
  }

  private static long now() {
    return System.currentTimeMillis();
  }
}
