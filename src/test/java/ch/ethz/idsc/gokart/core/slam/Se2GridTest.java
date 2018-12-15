// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Degree;
import junit.framework.TestCase;

public class Se2GridTest extends TestCase {
  public void testSimple() {
    Se2Grid se2Grid = new Se2Grid(RealScalar.of(2), Degree.of(1), 1);
    List<Se2GridPoint> gridPoints = se2Grid.gridPoints();
    assertEquals(gridPoints.size(), 27);
    try {
      gridPoints.clear();
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
