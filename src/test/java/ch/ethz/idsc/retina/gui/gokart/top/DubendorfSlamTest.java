// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import junit.framework.TestCase;

public class DubendorfSlamTest extends TestCase {
  public void testSimple() {
    assertEquals(DubendorfSlam.SE2MULTIRESSAMPLES.levels(), 4);
    for (int index = 0; index < 4; ++index) {
      int length = DubendorfSlam.SE2MULTIRESSAMPLES.level(index).length();
      assertEquals(length, 125);
    }
  }
}
