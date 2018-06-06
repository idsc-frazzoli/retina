// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import junit.framework.TestCase;

public class Se2MultiresGridsTest extends TestCase {
  public void testSimple() {
    Se2MultiresGrids se2MultiresGrids = LocalizationConfig.GLOBAL.createSe2MultiresGrids();
    assertEquals(se2MultiresGrids.grids(), 4);
    for (int index = 0; index < 4; ++index) {
      int length = se2MultiresGrids.grid(index).gridPoints().size();
      // System.out.println(length);
      assertEquals(length, 27);
    }
  }
}
