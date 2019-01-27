// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.opt.BSplineFunction;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class UniformBSpline2Test extends TestCase {
  public void testBPOutside() {
    assertEquals(UniformBSpline2.getBasisFunction(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(UniformBSpline2.getBasisFunction(RealScalar.of(+0)), RealScalar.ZERO);
    assertEquals(UniformBSpline2.getBasisFunction(RealScalar.of(+3)), RealScalar.ZERO);
    assertEquals(UniformBSpline2.getBasisFunction(RealScalar.of(+4)), RealScalar.ZERO);
  }

  public void testBPs1() {
    Scalar scalar = UniformBSpline2.getBasisFunction(RationalScalar.of(1, 3));
    Chop._12.requireClose(scalar, DoubleScalar.of(0.05555555555555555));
    BSplineFunction bSplineFunction = BSplineFunction.of(2, UnitVector.of(11, 5));
    ScalarUnaryOperator shift1 = s -> s.add(RationalScalar.of(3, 2));
    ScalarUnaryOperator shift2 = s -> s.add(RationalScalar.of(5, 1));
    for (Tensor _x : Subdivide.of(-2, 2, 200)) {
      Scalar x = _x.Get();
      Scalar v1 = UniformBSpline2.getBasisFunction(shift1.apply(x));
      Scalar v2 = (Scalar) bSplineFunction.apply(shift2.apply(x));
      Chop._10.requireClose(v1, v2);
    }
  }

  public void testBPs2() {
    Scalar scalar = UniformBSpline2.getBasisFunction(RationalScalar.of(4, 3));
    Chop._12.requireClose(scalar, DoubleScalar.of(0.722222222222222));
  }

  public void testBPs3() {
    Scalar scalar = UniformBSpline2.getBasisFunction(RationalScalar.of(7, 3));
    Chop._12.requireClose(scalar, DoubleScalar.of(0.2222222222222222));
  }

  public void testBPD1Outside() {
    assertEquals(UniformBSpline2.getBasisFunction1Der(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(UniformBSpline2.getBasisFunction1Der(RealScalar.of(+0)), RealScalar.ZERO);
    assertEquals(UniformBSpline2.getBasisFunction1Der(RealScalar.of(+3)), RealScalar.ZERO);
    assertEquals(UniformBSpline2.getBasisFunction1Der(RealScalar.of(+4)), RealScalar.ZERO);
  }

  public void testBPD1s1() {
    Scalar scalar = UniformBSpline2.getBasisFunction1Der(RationalScalar.of(1, 3));
    assertEquals(scalar, RationalScalar.of(1, 3));
  }

  public void testBPD1s2() {
    Scalar scalar = UniformBSpline2.getBasisFunction1Der(RationalScalar.of(4, 3));
    assertTrue(Chop._12.close(scalar, RationalScalar.of(1, 3)));
  }

  public void testBPD1s3() {
    Scalar scalar = UniformBSpline2.getBasisFunction1Der(RationalScalar.of(7, 3));
    assertTrue(Chop._12.close(scalar, RationalScalar.of(-2, 3)));
  }

  public void testBPD2Outside() {
    assertEquals(UniformBSpline2.getBasisFunction2Der(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(UniformBSpline2.getBasisFunction2Der(RealScalar.of(+0)), RealScalar.ONE);
    assertEquals(UniformBSpline2.getBasisFunction2Der(RealScalar.of(+3)), RealScalar.ZERO);
    assertEquals(UniformBSpline2.getBasisFunction2Der(RealScalar.of(+4)), RealScalar.ZERO);
  }

  public void testBPD2s1() {
    Scalar scalar = UniformBSpline2.getBasisFunction2Der(RationalScalar.of(1, 3));
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testBPD2s2() {
    Scalar scalar = UniformBSpline2.getBasisFunction2Der(RationalScalar.of(4, 3));
    Chop._12.requireClose(scalar, RealScalar.of(-2));
  }

  public void testBPD2s3() {
    Scalar scalar = UniformBSpline2.getBasisFunction2Der(RationalScalar.of(7, 3));
    Chop._12.requireClose(scalar, RealScalar.ONE);
  }

  public void testBasisMatrix() {
    for (int n = 2; n < 6; ++n) {
      Tensor pos = Tensors.vector(0.2, 0.4, 1.3);
      Tensor matrix = UniformBSpline2.getBasisMatrix(n, 0, false, pos);
      Tensor vector = Array.of(l -> RealScalar.ONE, n);
      Tensor dot = matrix.dot(vector);
      Chop._12.requireClose(dot, Array.of(l -> RealScalar.ONE, pos.length()));
    }
  }
}
