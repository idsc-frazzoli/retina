// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import junit.framework.TestCase;

public class SteerInitButtonTest extends TestCase {
  public void testEnabled() {
    SteerInitButton lib = new SteerInitButton();
    assertFalse(lib.isEnabled());
    lib.putEvent(null);
    assertTrue(lib.isEnabled());
  }

  public void testStartStop() {
    SteerInitButton lib = new SteerInitButton();
    lib.start();
    lib.stop();
  }
}
