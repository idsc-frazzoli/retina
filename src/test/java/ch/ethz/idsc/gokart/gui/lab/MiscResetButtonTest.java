// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import junit.framework.TestCase;

public class MiscResetButtonTest extends TestCase {
  public void testEnabled() {
    MiscResetButton lib = new MiscResetButton();
    assertFalse(lib.isEnabled());
  }
}
