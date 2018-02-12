// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import junit.framework.TestCase;

public class DubendorfSlamTest extends TestCase {
  public void testSimple() {
    assertEquals(DubendorfSlam.SE2MULTIRESGRIDS.grids(), 4);
    for (int index = 0; index < 4; ++index) {
      int length = DubendorfSlam.SE2MULTIRESGRIDS.grid(index).gridPoints().size();
      assertEquals(length, 125);
    }
  }
}
