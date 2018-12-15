package ch.ethz.idsc.retina.dev.joystick;

import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class ManualControlModule extends AbstractModule {
  private final ManualControlProvider manualControlProvider = JoystickConfig.GLOBAL.createProvider();

  @Override
  protected void first() throws Exception {
  }

  @Override
  protected void last() {
    // TODO Auto-generated method stub
  }
}
