// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Roots;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class InverseSteerCubicTest extends TestCase {
  public void testSteer() {
    Scalar b = RealScalar.of(+0.8284521034333863);
    Scalar d = RealScalar.of(-0.33633373640449604);
    Tensor coeffs = Tensors.of(RealScalar.ZERO, b, RealScalar.ZERO, d);
    ScalarUnaryOperator cubic = Series.of(coeffs);
    for (Tensor t : Subdivide.of(-0.75, 0.75, 1230)) {
      Scalar apply = cubic.apply(t.Get());
      Scalar root = Chop._10.apply(Roots.of(Tensors.of(apply.negate(), b, RealScalar.ZERO, d)).Get(2));
      Chop._13.requireClose(root, t.Get());
    }
  }

  public void testCubicOp() {
    Scalar b = RealScalar.of(+0.8284521034333863);
    Scalar d = RealScalar.of(-0.33633373640449604);
    InverseSteerCubic inverseSteerCubic = new InverseSteerCubic(b, d);
    Tensor coeffs = Tensors.of(RealScalar.ZERO, b, RealScalar.ZERO, d);
    ScalarUnaryOperator cubic = Series.of(coeffs);
    for (Tensor t : Subdivide.of(-0.75, 0.75, 1230)) {
      Scalar apply = cubic.apply(t.Get());
      Scalar root = inverseSteerCubic.apply(apply);
      Chop._13.requireClose(root, t.Get());
    }
    assertEquals(inverseSteerCubic.apply(RealScalar.ZERO), RealScalar.ZERO);
  }
}
