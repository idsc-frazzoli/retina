// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
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
  protected double breakStrength() {
    return Math.max( //
        _joystick.getLeftSliderUnitValue(), //
        _joystick.getRightSliderUnitValue());
  }

  /** tire speed */
  private final RimoPutProvider rimoPutProvider = new RimoPutProvider() {
    @Override
    public Optional<RimoPutEvent> putEvent() {
      if (hasJoystick()) {
        Scalar speed = getSpeedLimit().multiply(RealScalar.of(_joystick.getLeftKnobDirectionUp()));
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
