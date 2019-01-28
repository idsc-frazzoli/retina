// code by mh, modifs by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Mod;

/** @author Marc Heim
 * 
 * quadratic splines */
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
  private static final Scalar _1_2 = RealScalar.of(0.5);
  private static final Scalar _1 = RealScalar.of(1.0);
  private static final Scalar _3_2 = RealScalar.of(1.5);
  private static final Scalar _2 = RealScalar.of(2.0);
  private static final Scalar MINUS_2 = RealScalar.of(-2.0);
  private static final Scalar _3 = RealScalar.of(3.0);

  /** quadratic bspline defined over the interval [0, 3]
   * 
   * the maximum is attained at parameter value 3/2 == 1.5
   * where the function evaluates to 3/4
   * 
   * @param value
   * @return */
  public static Scalar getBasisFunction(Scalar value) {
    if (Scalars.lessThan(value, _0))
      return _0;
    if (Scalars.lessThan(value, _1))
      return _1_2.multiply(value).multiply(value);
    if (Scalars.lessThan(value, _2))
      return _3.multiply(value).subtract(_3_2).subtract(value.multiply(value));
    if (Scalars.lessThan(value, _3)) {
      Scalar factor = _3.subtract(value);
      return _1_2.multiply(factor).multiply(factor);
    }
    return _0;
  }

  public static Scalar getBasisFunction1Der(Scalar value) {
    if (Scalars.lessThan(value, _0))
      return _0;
    if (Scalars.lessThan(value, _1))
      return value;
    if (Scalars.lessThan(value, _2))
      return _3.subtract(value).subtract(value);
    if (Scalars.lessThan(value, _3))
      return value.subtract(_3);
    return _0;
  }

  public static Scalar getBasisFunction2Der(Scalar value) {
    if (Scalars.lessThan(value, _0))
      return _0;
    if (Scalars.lessThan(value, _1))
      return _1;
    if (Scalars.lessThan(value, _2))
      return MINUS_2;
    if (Scalars.lessThan(value, _3))
      return _1;
    return _0;
  }

  public static Tensor getBasisMatrix(int n, int der, boolean circle, Tensor queryPositions) {
    return queryPositions.map(value -> getBasisVector(n, der, circle, value));
  }

  public static Tensor getBasisVector(int n, int der, boolean circle, final Scalar x) {
    Scalar xx = circle //
        ? x
        : Clip.function(0, n - 2).apply(x);
    return Tensors.vector(i -> getBasisElement(n, i, xx, der, circle), n);
  }

  private static Scalar getBasisElement(int n, int i, Scalar x, int der, boolean circle) {
    Scalar value = x.subtract(RealScalar.of(i)).add(_2);
    if (circle)
      value = Mod.function(n).apply(value);
    if (der == 0)
      return getBasisFunction(value);
    if (der == 1)
      return getBasisFunction1Der(value);
    if (der == 2)
      return getBasisFunction2Der(value);
    return _0; // <- true and not a hack
  }
}
