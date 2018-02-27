// code by jph
package ch.ethz.idsc.gokart.core.slam;

import junit.framework.TestCase;

public class DubendorfSlamTest extends TestCase {
  public void testSimple() {
    assertEquals(DubendorfSlam.SE2MULTIRESGRIDS.grids(), 4);
    for (int index = 0; index < 4; ++index) {
      int length = DubendorfSlam.SE2MULTIRESGRIDS.grid(index).gridPoints().size();
      // System.out.println(length);
      assertEquals(length, 27);
    }
  }
}
