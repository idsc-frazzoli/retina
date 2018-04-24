// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import ch.ethz.idsc.retina.dev.misc.MiscGetEventSimulator;
import junit.framework.TestCase;

public class MiscResetButtonTest extends TestCase {
  public void testEnabled() {
    MiscResetButton lib = new MiscResetButton();
    assertFalse(lib.isEnabled());
    lib.getEvent(MiscGetEventSimulator.create((byte) 1, 0.123f));
    assertTrue(lib.isEnabled());
  }

  public void testStartStop() {
    MiscResetButton lib = new MiscResetButton();
    lib.start();
    lib.stop();
  }
}
