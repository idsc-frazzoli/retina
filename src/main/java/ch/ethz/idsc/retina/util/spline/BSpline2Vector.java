// code by mh
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public abstract class BSpline2Vector implements ScalarTensorFunction {
  static final Scalar _2 = RealScalar.of(2.0);
  private static final ScalarUnaryOperator[] BSPLINE2D = { //
      BSpline2D0.FUNCTION, //
      BSpline2D1.FUNCTION, //
      BSpline2D2.FUNCTION, //
      BSpline2Dn.FUNCTION //
  };

  public static ScalarTensorFunction of(int n, int der, boolean cyclic) {
    return cyclic //
        ? new CyclicBSpline2Vector(n, der)
        : new StringBSpline2Vector(n, der);
  }

  // ---
  final int n;
  final ScalarUnaryOperator bSpline2D;

  protected BSpline2Vector(int n, int der) {
    this.n = n;
    bSpline2D = BSPLINE2D[Math.min(der, 3)];
  }
}
