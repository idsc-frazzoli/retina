// code by mh
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** quadratic bspline defined over the interval [0, 3]
 * 
 * <p>The maximum is attained at parameter value 3/2 == 1.5
 * where the function evaluates to 3/4
 * 
 * <p>Reference:
 * Master Thesis Marc Heim, p. 28, eq. 3.6 */
/* package */ enum BSpline2D0 implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1_2 = RealScalar.of(0.5);
  private static final Scalar _1 = RealScalar.of(1.0);
  private static final Scalar _3_2 = RealScalar.of(1.5);
  private static final Scalar _2 = RealScalar.of(2.0);
  private static final Scalar _3 = RealScalar.of(3.0);

  @Override
  public Scalar apply(Scalar value) {
    if (Scalars.lessThan(value, _0))
      return _0;
    if (Scalars.lessThan(value, _1))
      // 0.5 u ^ 2
      return _1_2.multiply(value).multiply(value);
    if (Scalars.lessThan(value, _2))
      // thesis contains typo, the correct expression is
      // 0.5 (-3 + 6u - 2u^2)
      return _3.multiply(value).subtract(_3_2).subtract(value.multiply(value));
    if (Scalars.lessThan(value, _3)) {
      // 0.5 (3 - u)^2
      Scalar factor = _3.subtract(value);
      return _1_2.multiply(factor).multiply(factor);
    }
    return _0;
  }
}
