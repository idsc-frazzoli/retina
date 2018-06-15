// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotCalibrationProvider;
import ch.ethz.idsc.retina.dev.misc.MiscIgnitionProvider;
import ch.ethz.idsc.retina.dev.steer.SteerCalibrationProvider;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;

/** the module monitors the reset button of the joystick.
 * when the button is presses by the operator, the module schedules
 * the calibration procedure for the devices that are not calibrated.
 * the devices are: misc, linmot, and steer. */
public class JoystickResetModule extends AbstractModule implements JoystickListener {
  // TODO use JoystickLcmProvider ?
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(GokartLcmChannel.JOYSTICK);

  @Override // from AbstractModule
  protected void first() throws Exception {
    joystickLcmClient.addListener(this);
    joystickLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    joystickLcmClient.stopSubscriptions();
  }

  @Override // from JoystickListener
  public void joystick(JoystickEvent joystickEvent) {
    GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) joystickEvent;
    if (gokartJoystickInterface.isResetPressed()) {
      if (MiscIgnitionProvider.INSTANCE.isScheduleSuggested())
        MiscIgnitionProvider.INSTANCE.schedule(); // reset misc comm
      // ---
      if (LinmotCalibrationProvider.INSTANCE.isScheduleSuggested())
        LinmotCalibrationProvider.INSTANCE.schedule(); // calibrate linmot
      // ---
      if (SteerCalibrationProvider.INSTANCE.isScheduleSuggested())
        SteerCalibrationProvider.INSTANCE.schedule(); // calibrate steering
    }
  }
}
