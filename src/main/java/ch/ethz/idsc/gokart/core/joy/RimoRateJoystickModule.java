// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoConfig;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerUno;
import ch.ethz.idsc.retina.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerMapping;
import ch.ethz.idsc.tensor.Scalar;

/** DO NOT USE THIS IMPLEMENTATION.
 * 
 * IN THE FUTURE, IT MAY BE DESIRABLE TO SPEED CONTROL
 * THE GOKART WITH THE JOYSTICK, BUT FOR NOW THE JOYSTICK
 * HAS PROVEN TO WORK BEST WITH DIRECT TORQUE CONTROL.
 * 
 * @see RimoTorqueJoystickModule
 * 
 * speed control via joystick
 * way of controlling gokart on first test day
 * mode is decommissioned
 * 
 * superseded by {@link RimoTorqueJoystickModule} */
/* package */ class RimoRateJoystickModule extends GuideJoystickModule<RimoPutEvent> {
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  /* package */ final RimoRateControllerWrap rimoRateControllerWrap = new RimoRateControllerUno();

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

  /***************************************************/
  @Override // from GuideJoystickModule
  Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, GokartJoystickInterface joystick) {
    Scalar speed = RimoConfig.GLOBAL.rateLimit.multiply(joystick.getAheadAverage());
    Scalar theta = steerMapping.getAngleFromSCE(steerColumnInterface);
    return rimoRateControllerWrap.iterate(speed, theta);
  }
}
