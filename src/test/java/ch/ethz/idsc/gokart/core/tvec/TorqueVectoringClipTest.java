// code by jph
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class TorqueVectoringClipTest extends TestCase {
  private static void _checkSym(Scalar s1, Scalar s2, Tensor vector) {
    assertEquals(TorqueVectoringClip.of(s1, s2), vector);
    assertEquals(TorqueVectoringClip.of(s2, s1), Reverse.of(vector));
    assertEquals(TorqueVectoringClip.of(s1.negate(), s2.negate()), vector.negate());
    assertEquals(TorqueVectoringClip.of(s2.negate(), s1.negate()), Reverse.of(vector.negate()));
  }

  public void testClip() {
    _checkSym(RealScalar.of(+0.25), RealScalar.ZERO, Tensors.vector(0.25, 0));
    _checkSym(RealScalar.of(+0.25), RealScalar.of(1), Tensors.vector(0.25, 1));
    _checkSym(RealScalar.of(+0.25), RealScalar.of(-0.5), Tensors.vector(0.25, -0.5));
    _checkSym(RealScalar.of(+1.25), RealScalar.ZERO, Tensors.vector(1, 0.25));
    _checkSym(RealScalar.of(-1.25), RealScalar.ZERO, Tensors.vector(-1, -0.25));
    _checkSym(RealScalar.of(-1.25), RealScalar.of(1.5), Tensors.vector(-0.75, 1));
    _checkSym(RealScalar.of(-0.00), RealScalar.of(-0.00), Tensors.vector(0, 0));
    _checkSym(RealScalar.of(+1.00), RealScalar.of(-0.00), Tensors.vector(1, 0));
    _checkSym(RealScalar.of(+1.00), RealScalar.of(-1.00), Tensors.vector(1, -1));
    _checkSym(RealScalar.of(-1.50), RealScalar.of(+1.25), Tensors.vector(-1, 0.75));
  }

  public void testClipRandom() {
    for (int count = 0; count < 100; ++count) {
      Distribution distribution = UniformDistribution.of(Clip.absoluteOne());
      Scalar s1 = RandomVariate.of(distribution);
      Scalar s2 = RandomVariate.of(distribution);
      _checkSym(s1, s2, Tensors.of(s1, s2));
    }
  }

  public void testUnitFail() {
    try {
      TorqueVectoringClip.of(Quantity.of(-1, SI.METER), Quantity.of(1, SI.METER));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
