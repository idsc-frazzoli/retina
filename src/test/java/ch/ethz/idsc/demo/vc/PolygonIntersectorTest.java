// code by jph
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class PolygonIntersectorTest extends TestCase {
  public void testSimple() {
    // {{2.0, 1.0}, {1.0, 1.0}, {1.0, 2.0}}
    // {{2.0, 1.0}, {1.0, 1.0}, {1.0, 1.5}, {1.3333333333333333, 1.6666666666666667}}
    {
      Tensor poly1 = Tensors.fromString("{{1,1},{2,1},{1,2}}");
      Tensor poly2 = Tensors.fromString("{{1,1},{2,1},{2,3},{1,2}}");
      Tensor res = PolygonIntersector.polygonIntersect(poly1, poly2);
      System.out.println(res);
    }
    {
      Tensor poly1 = Tensors.fromString("{{1,1},{2,1},{1,2}}");
      Tensor poly2 = Tensors.fromString("{{0,1},{2,1},{2,2}}");
      Tensor res = PolygonIntersector.polygonIntersect(poly1, poly2);
      System.out.println(res);
    }
  }
}
