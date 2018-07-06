// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class EnlargedPointsTest extends TestCase {
  public void testRectangles() {
    Tensor hulls = Tensors.fromString("{ {{0,0},{1,0},{1,1},{0,1}}, {{2,0},{3,0},{3,2},{2,2}}}");
    EnlargedPoints test = new EnlargedPoints(hulls);
    assertEquals(test.getTotalArea(), 3.0);
  }

  public void testSimple() {
    Tensor hulls = Tensors.fromString("{ {{-1,0},{0,2},{1,0},{0,-2}}, {{2,0},{3,0},{3,2},{0,2}} }");
    EnlargedPoints enlargedPoints = new EnlargedPoints(hulls);
    assertEquals(enlargedPoints.getTotalArea(), 8.0);
  }
}
