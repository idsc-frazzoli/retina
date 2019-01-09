// code by jph
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class TorqueVectoringHelperTest extends TestCase {
  private static void _checkSym(Scalar s1, Scalar s2, Tensor vector) {
    assertEquals(TorqueVectoringHelper.clip(s1, s2), vector);
    assertEquals(TorqueVectoringHelper.clip(s2, s1), Reverse.of(vector));
  }

  public void testClip1() {
    _checkSym(RealScalar.of(+1.25), RealScalar.ZERO, Tensors.vector(1, 0.25));
    _checkSym(RealScalar.of(+1.25), RealScalar.of(1.5), Tensors.vector(1, 1));
    _checkSym(RealScalar.of(-1.25), RealScalar.ZERO, Tensors.vector(-1, -0.25));
    _checkSym(RealScalar.of(-1.25), RealScalar.of(1.5), Tensors.vector(-0.75, 1));
    _checkSym(RealScalar.of(-1.50), RealScalar.of(-1.25), Tensors.vector(-1, -1));
    _checkSym(RealScalar.of(-0.00), RealScalar.of(-0.00), Tensors.vector(0, 0));
  }

  public void testClip2a() {
    Tensor tensor = TorqueVectoringHelper.clip(RealScalar.of(-1.5), RealScalar.of(1.25));
    assertEquals(tensor, Tensors.vector(-1, 1));
  }

  public void testClip2b() {
    Tensor tensor = TorqueVectoringHelper.clip(RealScalar.of(1.25), RealScalar.of(-1.5));
    assertEquals(tensor, Tensors.vector(1, -1));
  }

  public void testClip3() {
    Distribution distribution = UniformDistribution.of(-1, 1);
    Scalar s1 = RandomVariate.of(distribution);
    Scalar s2 = RandomVariate.of(distribution);
    _checkSym(s1, s2, Tensors.of(s1, s2));
  }
}
