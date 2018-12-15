// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.dev.GokartActuatorCalibration;
import ch.ethz.idsc.gokart.dev.ManualControlSingleton;
import ch.ethz.idsc.retina.dev.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.dev.joystick.ManualControlListener;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** the module monitors the reset button of the joystick.
 * when the button is presses by the operator, the module schedules
 * the calibration procedure for the devices that are not calibrated.
 * the devices are: misc, linmot, and steer. */
public class JoystickResetModule extends AbstractModule implements ManualControlListener {
  @Override // from AbstractModule
  protected void first() throws Exception {
    ManualControlSingleton.INSTANCE.addListener(this);
  }

  @Override // from AbstractModule
  protected void last() {
    ManualControlSingleton.INSTANCE.removeListener(this);
  }

  @Override
  public void manualControl(ManualControlInterface manualControlInterface) {
    if (manualControlInterface.isResetPressed())
      GokartActuatorCalibration.all();
  }
}
