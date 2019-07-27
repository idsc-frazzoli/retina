// code by jph
package ch.ethz.idsc.sophus.itp;

import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class ArcLengthParametrizationTest extends TestCase {
  public void testSimpleR2String() {
    // ArcLengthParametrization.of(Tensors.ve, splitInterface, tensor)
  }

  public void testFailLength() {
    try {
      ArcLengthParametrization.of(Tensors.vector(1, 2, 3), RnGeodesic.INSTANCE, Tensors.vector(1, 2, 3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailNonVector() {
    try {
      ArcLengthParametrization.of(HilbertMatrix.of(3), RnGeodesic.INSTANCE, Tensors.vector(1, 2, 3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testFailNull() {
    try {
      ArcLengthParametrization.of(Tensors.vector(1, 2, 3), null, Tensors.vector(1, 2, 3, 4));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
