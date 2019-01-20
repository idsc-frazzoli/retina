// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.dev.rimo.RimoConfig;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerUno;
import ch.ethz.idsc.gokart.dev.rimo.RimoRateControllerWrap;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.tensor.Scalar;

/** DO NOT USE THIS IMPLEMENTATION.
 * 
 * IN THE FUTURE, IT MAY BE DESIRABLE TO SPEED CONTROL
 * THE GOKART WITH THE JOYSTICK, BUT FOR NOW THE JOYSTICK
 * HAS PROVEN TO WORK BEST WITH DIRECT TORQUE CONTROL.
 * 
 * @see RimoTorqueManualModule
 * 
 * speed control via joystick
 * way of controlling gokart on first test day
 * mode is decommissioned
 * 
 * superseded by {@link RimoTorqueManualModule} */
/* package */ class RimoRateManualModule extends GuideManualModule<RimoPutEvent> {
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
      SteerColumnInterface steerColumnInterface, ManualControlInterface manualControlInterface) {
    Scalar speed = RimoConfig.GLOBAL.rateLimit.multiply(manualControlInterface.getAheadAverage());
    Scalar theta = steerMapping.getAngleFromSCE(steerColumnInterface);
    return rimoRateControllerWrap.iterate(speed, theta);
  }
}
