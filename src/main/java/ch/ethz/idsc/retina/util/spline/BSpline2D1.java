// code by mh
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** 1st derivative at given value of quadratic BSpline with support in interval [0, 3]
 * 
 * confirmed with Mathematica */
/* package */ enum BSpline2D1 implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1 = RealScalar.of(1.0);
  private static final Scalar _2 = RealScalar.of(2.0);
  private static final Scalar _3 = RealScalar.of(3.0);

  @Override
  public Scalar apply(Scalar value) {
    if (Scalars.lessThan(value, _0))
      return _0;
    if (Scalars.lessThan(value, _1))
      return value;
    if (Scalars.lessThan(value, _2))
      return _3.subtract(value.add(value));
    if (Scalars.lessThan(value, _3))
      return value.subtract(_3);
    return _0;
  }
}
