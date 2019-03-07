// code by jph
package ch.ethz.idsc.gokart.core.man;

import junit.framework.TestCase;

public class ManualResetModuleTest extends TestCase {
  public void testSimple() throws Exception {
    ManualResetModule joystickResetModule = new ManualResetModule();
    joystickResetModule.first();
    joystickResetModule.last();
  }
}
