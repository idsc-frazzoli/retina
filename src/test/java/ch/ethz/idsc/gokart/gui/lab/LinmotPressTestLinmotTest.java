// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class LinmotPressTestLinmotTest extends TestCase {
  public void testSimple() {
    LinmotPressTestLinmot linmotPressTestLinmot = new LinmotPressTestLinmot();
    assertFalse(linmotPressTestLinmot.putEvent().isPresent());
    linmotPressTestLinmot.startPress(RealScalar.of(.3));
    assertTrue(linmotPressTestLinmot.putEvent().isPresent());
    linmotPressTestLinmot.stopPress();
    assertFalse(linmotPressTestLinmot.putEvent().isPresent());
  }
}
