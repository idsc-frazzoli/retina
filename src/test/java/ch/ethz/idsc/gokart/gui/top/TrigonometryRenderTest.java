// code by jph
package ch.ethz.idsc.gokart.gui.top;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import junit.framework.TestCase;

public class TrigonometryRenderTest extends TestCase {
  public void testSimple() {
    Tensor matrix = Se2Matrix.of(Tensors.vector(0, 0, 0));
    assertEquals(matrix, IdentityMatrix.of(3));
  }
}
