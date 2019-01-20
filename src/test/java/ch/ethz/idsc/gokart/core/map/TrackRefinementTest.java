// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import junit.framework.TestCase;

public class TrackRefinementTest extends TestCase {
  public void testSimple() {
    int n = 10;
    int m = 5;
    Tensor vector = Tensors.vector((i) -> RealScalar.of((n - 2.0) * (i / (m - 1.0))), m);
    // Tensors.vector(i -> RealScalar.of((n + 0.0) * (i / (m + 0.0))), m);
    assertEquals(vector, Subdivide.of(0, n - 2, m - 1));
  }
}
