// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2SamplerTest extends TestCase {
  public void testSome() {
    Tensor matrix = Se2Sampler.get(RealScalar.of(0), RealScalar.of(2), RealScalar.of(3));
    Tensor diag = Diagonal.of(matrix);
    assertEquals(diag, Tensors.vector(1, 1, 1));
  }

  public void testSome2() {
    Tensor mat = Se2Sampler.get(1, 0, 0);
    // System.out.println(Pretty.of(mat));
    Tensor rot = RotationMatrix.of(RealScalar.ONE);
    // System.out.println(Pretty.of(rot));
    assertTrue(Chop._12.close(mat.Get(0, 1), rot.Get(0, 1)));
    assertTrue(Chop._12.close(mat.Get(1, 1), rot.Get(1, 1)));
  }
}
