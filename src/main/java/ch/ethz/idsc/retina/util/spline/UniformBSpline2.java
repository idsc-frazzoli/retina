// code by mh, modifs by jph
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Mod;

/** quadratic splines */
public enum UniformBSpline2 {
  ;
  // based on matlab code:
  /* function [xx,yy] = casadiDynamicBSPLINE(x,points)
   * [n,~] = size(points);
   * x = max(x,0);
   * x = min(x,n-2);
   * import casadi.*
   * %position in basis function
   * if isa(x, 'double')
   * v = zeros(n,1);
   * b = zeros(n,1);
   * else
   * v = SX.zeros(n,1);
   * b = SX.zeros(n,1);
   * end
   * for i = 1:n
   * v(i,1)=x-i+3;
   * vv = v(i,1);
   * if isa(vv, 'double')
   * if vv<0
   * b(i,1)=0;
   * elseif vv<1
   * b(i,1)=0.5*vv^2;
   * elseif vv<2
   * b(i,1)=0.5*(-3+6*vv-2*vv^2);
   * elseif vv<3
   * b(i,1)=0.5*(3-vv)^2;
   * else
   * b(i,1)=0;
   * end
   * else
   * b(i,1) = if_else(vv<0,0,...
   * if_else(vv<1,0.5*vv^2,...
   * if_else(vv<2,0.5*(-3+6*vv-2*vv^2),...
   * if_else(vv<3,0.5*(3-vv)^2,0))));
   * end
   * end
   * xx = b'*points(:,1);
   * yy = b'*points(:,2);
   * end */
  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _2 = RealScalar.of(2.0);

  /***************************************************/
  public static Tensor getBasisMatrix(int n, int der, boolean circle, Tensor queryPositions) {
    return queryPositions.map(value -> getBasisVector(n, der, circle, value));
  }

  public static Tensor getBasisVector(int n, int der, boolean circle, final Scalar x) {
    Scalar xx = circle //
        ? x
        : Clips.interval(0, n - 2).apply(x);
    return Tensors.vector(i -> getBasisElement(n, i, xx, der, circle), n);
  }

  private static Scalar getBasisElement(int n, int i, Scalar x, int der, boolean circle) {
    Scalar value = x.subtract(RealScalar.of(i)).add(_2);
    if (circle)
      value = Mod.function(n).apply(value);
    if (der == 0)
      return BSpline2D0.FUNCTION.apply(value);
    if (der == 1)
      return BSpline2D1.FUNCTION.apply(value);
    if (der == 2)
      return BSpline2D2.FUNCTION.apply(value);
    return _0; // <- true and not a hack
  }
}
