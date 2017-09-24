// code by jph
package ch.ethz.idsc.retina.lcm.joystick;

import ch.ethz.idsc.retina.dev.joystick.JoystickListener;
import ch.ethz.idsc.retina.dev.joystick.JoystickType;

/** singleton instance */
public enum GenericXboxPadLcmClient {
  INSTANCE;
  // ---
  private final JoystickLcmClient joystickLcmClient = new JoystickLcmClient(JoystickType.GENERIC_XBOX_PAD);

  private GenericXboxPadLcmClient() {
    joystickLcmClient.startSubscriptions();
  }

  public void addListener(JoystickListener joystickListener) {
    joystickLcmClient.addListener(joystickListener);
  }

  public void removeListener(JoystickListener joystickListener) {
    joystickLcmClient.removeListener(joystickListener);
  }
}
