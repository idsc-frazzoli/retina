// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import java.util.Optional;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoConfig;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** speed control via joystick
 * way of controlling gokart on first test day
 * mode is decommissioned
 * 
 * superseded by {@link RimoTorqueJoystickModule} */
/* package */ class RimoRateJoystickModule extends JoystickModule<RimoPutEvent> {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  /* package */ final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerWrap();

  @Override // from AbstractModule
  void protected_first() {
    RimoSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(rimoRateControllerWrap);
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(rimoRateControllerWrap);
  }

  @Override // from JoystickModule
  Optional<RimoPutEvent> translate(GokartJoystickInterface joystick) {
    return control(steerColumnInterface, joystick);
  }

  /** @param steerColumnInterface
   * @param joystick
   * @return */
  /* package */ Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, //
      GokartJoystickInterface joystick) {
    if (steerColumnInterface.isSteerColumnCalibrated()) {
      Scalar speed = RimoConfig.GLOBAL.rateLimit.multiply(joystick.getAheadAverage());
      DifferentialSpeed differentialSpeed = ChassisGeometry.GLOBAL.getDifferentialSpeed();
      Scalar theta = SteerConfig.GLOBAL.getAngleFromSCE(steerColumnInterface);
      Tensor pair = differentialSpeed.pair(speed, theta);
      Tensor bias = joystick.getAheadPair_Unit().multiply(RimoConfig.GLOBAL.rateLimit);
      return rimoRateControllerWrap.iterate(pair.add(bias));
    }
    return Optional.empty();
  }
}
