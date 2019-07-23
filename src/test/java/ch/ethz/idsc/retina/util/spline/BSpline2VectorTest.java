// code by jph
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BSpline2VectorTest extends TestCase {
  public void testBasisMatrix() {
    for (int n = 2; n < 6; ++n) {
      Tensor pos = Tensors.vector(0.2, 0.4, 1.3);
      Tensor matrix = pos.map(BSpline2Vector.of(n, 0, false));
      Tensor vector = Array.of(l -> RealScalar.ONE, n);
      Tensor dot = matrix.dot(vector);
      Chop._12.requireClose(dot, Array.of(l -> RealScalar.ONE, pos.length()));
    }
  }

  public void testDerivativeFail() {
    try {
      BSpline2Vector.of(10, -1, false);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      BSpline2Vector.of(10, -1, true);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
