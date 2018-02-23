// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.red.Diagonal;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2ExpTest extends TestCase {
  public void testSome() {
    Tensor matrix = Se2Exp.of(Tensors.vector(2, 3, 0));
    Tensor diag = Diagonal.of(matrix);
    assertEquals(diag, Tensors.vector(1, 1, 1));
  }

  public void testSome2() {
    Tensor matrix = Se2Exp.of(UnitVector.of(3, 2));
    Tensor rot = RotationMatrix.of(RealScalar.ONE);
    // System.out.println(Pretty.of(rot));
    assertTrue(Chop._12.close(matrix.Get(0, 1), rot.Get(0, 1)));
    assertTrue(Chop._12.close(matrix.Get(1, 1), rot.Get(1, 1)));
  }
}
