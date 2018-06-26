// code by jph
package ch.ethz.idsc.gokart.core.joy;

import junit.framework.TestCase;

public class JoystickResetModuleTest extends TestCase {
  public void testSimple() throws Exception {
    JoystickResetModule joystickResetModule = new JoystickResetModule();
    joystickResetModule.first();
    joystickResetModule.last();
  }
}
