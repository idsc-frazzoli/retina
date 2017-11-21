// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.dev.rimo.RimoGetTire;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** position control for steering
 * differential speed on rear wheels according to steering angle
 * 
 * TODO NRJ still uses velocity control for RIMO */
public class HmiSimpleDriveJoystick extends HmiAbstractJoystick {
  @Override
  protected double breakStrength() {
    return Math.max( //
        _joystick.getLeftSliderUnitValue(), //
        _joystick.getRightSliderUnitValue());
  }

  /** tire speed */
  private final RimoPutProvider rimoPutProvider = new RimoPutProvider() {
    // TODO geh vom gas falls bremse gedrueckt ist
    @Override
    public Optional<RimoPutEvent> putEvent() {
      Scalar speed = Quantity.of(0, RimoGetTire.UNIT_RATE);
      if (hasJoystick())
        speed = getSpeedLimit().multiply(RealScalar.of(_joystick.getLeftKnobDirectionUp()));
      if (hasJoystick()) {
        // GenericXboxPadJoystick joystick = _joystick;
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
