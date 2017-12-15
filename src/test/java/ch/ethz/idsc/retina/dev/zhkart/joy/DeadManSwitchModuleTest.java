// code by jph
package ch.ethz.idsc.retina.dev.zhkart.joy;

import junit.framework.TestCase;

public class DeadManSwitchModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    DeadManSwitchModule dmsm = new DeadManSwitchModule();
    dmsm.first();
    dmsm.last();
  }

  public void testPutEvent() {
    DeadManSwitchModule dmsm = new DeadManSwitchModule();
    assertFalse(dmsm.putEvent().isPresent());
  }
}
