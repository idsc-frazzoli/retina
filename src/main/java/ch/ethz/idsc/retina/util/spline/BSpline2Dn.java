// code by mh
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** 3rd and higher derivatives at given value of quadratic BSpline with support in interval [0, 3] */
/* package */ enum BSpline2Dn implements ScalarUnaryOperator {
  FUNCTION;
  // ---
  private static final Scalar _0 = RealScalar.of(0.0);

  @Override
  public Scalar apply(Scalar value) {
    return _0;
  }
}
