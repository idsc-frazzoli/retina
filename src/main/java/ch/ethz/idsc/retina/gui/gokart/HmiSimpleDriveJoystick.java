// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

/** position control for steering
 * differential speed on rear wheels according to steering angle */
public class HmiSimpleDriveJoystick extends HmiAbstractJoystick {
  @Override
  protected double breakStrength(GokartJoystickInterface gokartJoystickInterface) {
    return gokartJoystickInterface.getBreakSecondary();
  }

  /** tire speed */
  private final RimoPutProvider rimoPutProvider = new RimoPutProvider() {
    @Override
    public Optional<RimoPutEvent> putEvent() {
      Optional<GokartJoystickInterface> optional = getJoystick();
      if (optional.isPresent()) {
        Scalar speed = getSpeedLimit().multiply(RealScalar.of(optional.get().getAheadAverage()));
        final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
        if (steerColumnTracker.isCalibrated()) {
          Scalar axisDelta = ChassisGeometry.GLOBAL.xAxleDistanceMeter();
          Scalar yTireRear = ChassisGeometry.GLOBAL.yTireRearMeter();
          DifferentialSpeed differentialSpeed = new DifferentialSpeed(axisDelta, yTireRear);
          Scalar theta = SteerConfig.getAngleFromSCE(steerColumnTracker.getEncoderValueCentered());
          return rimoRateControllerWrap.iterate(differentialSpeed.pair(speed, theta));
        }
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.MANUAL;
    }
  };

  @Override
  public RimoPutProvider getRimoPutProvider() {
    return rimoPutProvider;
  }
}
