// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotCalibrationProvider;
import ch.ethz.idsc.retina.dev.misc.MiscIgnitionProvider;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class JoystickResetModule extends AbstractModule implements JoystickListener {
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
      // reset misc comm
      if (MiscIgnitionProvider.INSTANCE.isCalibrationSuggested())
        MiscIgnitionProvider.INSTANCE.schedule();
      // calibate linmot
      if (LinmotCalibrationProvider.INSTANCE.isCalibrationSuggested())
        LinmotCalibrationProvider.INSTANCE.schedule();
    }
  }
}
