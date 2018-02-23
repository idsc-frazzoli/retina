// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.retina.sys.ModuleAuto;
import junit.framework.TestCase;

public class JoystickGroupModuleTest extends TestCase {
  public void testSize() {
    assertEquals(new JoystickGroupModule().modules().size(), 3);
  }

  public void testSimple() throws Exception {
    ModuleAuto.INSTANCE.runOne(JoystickGroupModule.class);
    Thread.sleep(50);
    ModuleAuto.INSTANCE.terminateOne(JoystickGroupModule.class);
  }
}
