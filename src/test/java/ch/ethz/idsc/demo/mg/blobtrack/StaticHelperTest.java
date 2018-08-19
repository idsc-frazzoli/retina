// code by jph
package ch.ethz.idsc.demo.mg.blobtrack;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.SymmetricMatrixQ;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testEv() {
    float[][] eigenVectors = StaticHelper.getEigenVectors(new double[][] { { 7, 2 }, { 2, 10 } });
    Tensor matrix = Tensors.matrixFloat(eigenVectors);
    assertTrue(SymmetricMatrixQ.of(matrix));
  }
}
