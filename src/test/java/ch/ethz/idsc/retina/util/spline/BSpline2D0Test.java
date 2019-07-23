// code by jph
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.opt.BSplineFunction;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;
import junit.framework.TestCase;

public class BSpline2D0Test extends TestCase {
  public void testBPOutside() {
    assertEquals(BSpline2D0.FUNCTION.apply(RealScalar.of(-1)), RealScalar.ZERO);
    assertEquals(BSpline2D0.FUNCTION.apply(RealScalar.of(+0)), RealScalar.ZERO);
    assertEquals(BSpline2D0.FUNCTION.apply(RealScalar.of(+3)), RealScalar.ZERO);
    assertEquals(BSpline2D0.FUNCTION.apply(RealScalar.of(+4)), RealScalar.ZERO);
  }

  public void testBPs1() {
    Scalar scalar = BSpline2D0.FUNCTION.apply(RationalScalar.of(1, 3));
    Chop._12.requireClose(scalar, DoubleScalar.of(0.05555555555555555));
    BSplineFunction bSplineFunction = BSplineFunction.of(2, UnitVector.of(11, 5));
    ScalarUnaryOperator shift1 = s -> s.add(RationalScalar.of(3, 2));
    ScalarUnaryOperator shift2 = s -> s.add(RationalScalar.of(5, 1));
    for (Tensor _x : Subdivide.of(-2, 2, 200)) {
      Scalar x = _x.Get();
      Scalar v1 = BSpline2D0.FUNCTION.apply(shift1.apply(x));
      Scalar v2 = (Scalar) bSplineFunction.apply(shift2.apply(x));
      Chop._10.requireClose(v1, v2);
    }
  }

  public void testBPs2() {
    Scalar scalar = BSpline2D0.FUNCTION.apply(RationalScalar.of(4, 3));
    Chop._12.requireClose(scalar, DoubleScalar.of(0.722222222222222));
  }

  public void testBPs3() {
    Scalar scalar = BSpline2D0.FUNCTION.apply(RationalScalar.of(7, 3));
    Chop._12.requireClose(scalar, DoubleScalar.of(0.2222222222222222));
  }
}
