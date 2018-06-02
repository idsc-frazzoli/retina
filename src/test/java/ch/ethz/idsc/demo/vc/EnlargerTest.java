// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class EnlargerTest extends TestCase {
  // {{0.0, 1.0}, {-0.0, -0.0}, {0.0, 0.0}, {0.5, 0.5}, {-0.0, 1.0}}
  // {{0.0, 1.0}, {-0.0, -0.0}, {0.0, 0.0}, {0.5, 0.5}, {-0.0, 1.0}}
  public void testSimple() {
    Tensor p = Tensors.fromString("{{{0,0},{1,0},{1,1},{0,1}}, {{0,0},{1,0},{0.5,0.5}}}");
    Enlarger test = new Enlarger(p);
    assertTrue(test.getTotalArea() == 1.25);
  }
}
