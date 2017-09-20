// code by jph
package ch.ethz.idsc.retina.alg.slam;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2ExpTest extends TestCase {
  public void testSome() {
    Tensor matrix = Se2Exp.of(RealScalar.of(2), RealScalar.of(3), RealScalar.ZERO);
    Tensor diag = Diagonal.of(matrix);
    assertEquals(diag, Tensors.vector(1, 1, 1));
  }

  public void testSome2() {
    Tensor matrix = Se2Exp.of(0, 0, 1);
    Tensor rot = RotationMatrix.of(RealScalar.ONE);
    // System.out.println(Pretty.of(rot));
    assertTrue(Chop._12.close(matrix.Get(0, 1), rot.Get(0, 1)));
    assertTrue(Chop._12.close(matrix.Get(1, 1), rot.Get(1, 1)));
  }
}
