// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class MPCBSplineTest extends TestCase {
  public void testBPOutside() {
    assertEquals(MPCBSpline.getBasisFunction(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(MPCBSpline.getBasisFunction(RealScalar.of(+0)), RealScalar.ZERO);
    assertEquals(MPCBSpline.getBasisFunction(RealScalar.of(+3)), RealScalar.ZERO);
    assertEquals(MPCBSpline.getBasisFunction(RealScalar.of(+4)), RealScalar.ZERO);
  }

  public void testBPs1() {
    Scalar scalar = MPCBSpline.getBasisFunction(RationalScalar.of(1, 3));
    assertTrue(Chop._12.close(scalar, DoubleScalar.of(0.05555555555555555)));
  }

  public void testBPs2() {
    Scalar scalar = MPCBSpline.getBasisFunction(RationalScalar.of(4, 3));
    assertTrue(Chop._12.close(scalar, DoubleScalar.of(0.722222222222222)));
  }

  public void testBPs3() {
    Scalar scalar = MPCBSpline.getBasisFunction(RationalScalar.of(7, 3));
    assertTrue(Chop._12.close(scalar, DoubleScalar.of(0.2222222222222222)));
  }

  public void testBPD1Outside() {
    assertEquals(MPCBSpline.getBasisFunction1Der(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(MPCBSpline.getBasisFunction1Der(RealScalar.of(+0)), RealScalar.ZERO);
    assertEquals(MPCBSpline.getBasisFunction1Der(RealScalar.of(+3)), RealScalar.ZERO);
    assertEquals(MPCBSpline.getBasisFunction1Der(RealScalar.of(+4)), RealScalar.ZERO);
  }

  public void testBPD1s1() {
    Scalar scalar = MPCBSpline.getBasisFunction1Der(RationalScalar.of(1, 3));
    assertEquals(scalar, RationalScalar.of(1, 3));
  }

  public void testBPD1s2() {
    Scalar scalar = MPCBSpline.getBasisFunction1Der(RationalScalar.of(4, 3));
    assertTrue(Chop._12.close(scalar, RationalScalar.of(1, 3)));
  }

  public void testBPD1s3() {
    Scalar scalar = MPCBSpline.getBasisFunction1Der(RationalScalar.of(7, 3));
    assertTrue(Chop._12.close(scalar, RationalScalar.of(-2, 3)));
  }

  public void testBPD2Outside() {
    assertEquals(MPCBSpline.getBasisFunction2Der(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(MPCBSpline.getBasisFunction2Der(RealScalar.of(+0)), RealScalar.ONE);
    assertEquals(MPCBSpline.getBasisFunction2Der(RealScalar.of(+3)), RealScalar.ZERO);
    assertEquals(MPCBSpline.getBasisFunction2Der(RealScalar.of(+4)), RealScalar.ZERO);
  }

  public void testBPD2s1() {
    Scalar scalar = MPCBSpline.getBasisFunction2Der(RationalScalar.of(1, 3));
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testBPD2s2() {
    Scalar scalar = MPCBSpline.getBasisFunction2Der(RationalScalar.of(4, 3));
    assertTrue(Chop._12.close(scalar, RealScalar.of(-2)));
  }

  public void testBPD2s3() {
    Scalar scalar = MPCBSpline.getBasisFunction2Der(RationalScalar.of(7, 3));
    assertTrue(Chop._12.close(scalar, RealScalar.ONE));
  }

  public void testBasisMatrix() {
    for (int n = 2; n < 6; ++n) {
      Tensor pos = Tensors.vector(0.2, 0.4, 1.3);
      Tensor matrix = MPCBSpline.getBasisMatrix(n, pos, 0, false);
      Tensor vector = Array.of(l -> RealScalar.ONE, n);
      Tensor dot = matrix.dot(vector);
      assertTrue(Chop._10.close(dot, Array.of(l -> RealScalar.ONE, pos.length())));
    }
  }
}
