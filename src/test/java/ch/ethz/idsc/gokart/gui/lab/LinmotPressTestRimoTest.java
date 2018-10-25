// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import junit.framework.TestCase;

public class LinmotPressTestRimoTest extends TestCase {
  public void testSimple() {
    LinmotPressTestRimo linmotPressTestRimo = new LinmotPressTestRimo();
    assertFalse(linmotPressTestRimo.putEvent().isPresent());
    linmotPressTestRimo.startPress();
    assertTrue(linmotPressTestRimo.putEvent().isPresent());
    linmotPressTestRimo.stopPress();
    assertFalse(linmotPressTestRimo.putEvent().isPresent());
  }
}
