// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.dev.GokartActuatorCalibration;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** the module monitors the reset button of the joystick.
 * when the button is presses by the operator, the module schedules
 * the calibration procedure for the devices that are not calibrated.
 * the devices are: misc, linmot, and steer. */
public class JoystickResetModule extends AbstractModule implements JoystickListener {
  /** use of joystick lcm client is sufficient over joystick lcm provider since the
   * JoystickEvent is processed in the callback function which ensures the message
   * is not outdated. */
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);

  @Override // from AbstractModule
  protected void first() throws Exception {
    joystickLcmClient.addListener(this);
    joystickLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    joystickLcmClient.stopSubscriptions();
    joystickLcmClient.removeListener(this);
  }

  @Override // from JoystickListener
  public void joystick(JoystickEvent joystickEvent) {
    GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystickEvent;
    if (gokartJoystickInterface.isResetPressed())
      GokartActuatorCalibration.all();
  }
}
