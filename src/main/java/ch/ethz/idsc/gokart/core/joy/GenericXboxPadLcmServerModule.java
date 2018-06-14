// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.retina.dev.joystick.JoystickType;

// TODO DUBILAB OWLY3D migration
public class GenericXboxPadLcmServerModule extends AbstractJoystickLcmServerModule {
  @Override
  public JoystickType getJoystickType() {
    return JoystickType.GENERIC_XBOX_PAD;
  }

  public static void main(String[] args) throws Exception {
    new GenericXboxPadLcmServerModule().first();
  }
}
