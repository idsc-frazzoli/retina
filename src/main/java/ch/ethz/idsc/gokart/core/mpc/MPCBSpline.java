//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.Power;

public class MPCBSpline {
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
  public static Scalar getBasisElement(int i, Scalar x) {
    Scalar vv = x.subtract(RealScalar.of(i)).add(RealScalar.of(3));
    if (Scalars.lessThan(vv, RealScalar.ZERO))
      return RealScalar.ZERO;
    else if (Scalars.lessThan(vv, RealScalar.ONE))
      return RealScalar.of(0.5).multiply(Power.of(vv, 2));
    else if (Scalars.lessThan(vv, RealScalar.of(2)))
      return RealScalar.of(0.5).multiply(//
          RealScalar.of(3).negate()//
              .add(RealScalar.of(6).multiply(vv)))//
          .add(RealScalar.of(2).multiply(Power.of(vv, 2)));
    else if (Scalars.lessThan(vv, RealScalar.of(2)))
      return RealScalar.of(0.5).multiply(Power.of(RealScalar.of(3).subtract(vv), 2));
    else
      return RealScalar.ZERO;
  }
  
  public static Scalar getBasisElement1Der(int i, Scalar x) {
    Scalar vv = x.subtract(RealScalar.of(i)).add(RealScalar.of(3));
    if (Scalars.lessThan(vv, RealScalar.ZERO))
      return RealScalar.ZERO;
    else if (Scalars.lessThan(vv, RealScalar.ONE))
      return vv;
    else if (Scalars.lessThan(vv, RealScalar.of(2)))
      return RealScalar.of(3).subtract(RealScalar.of(2).multiply(vv));
    else if (Scalars.lessThan(vv, RealScalar.of(2)))
      return RealScalar.of(3).negate().add(vv);
    else
      return RealScalar.ZERO;
  }

  public static Scalar getBasisElement2Der(int i, Scalar x) {
    Scalar vv = x.subtract(RealScalar.of(i)).add(RealScalar.of(3));
    if (Scalars.lessThan(vv, RealScalar.ZERO))
      return RealScalar.ZERO;
    else if (Scalars.lessThan(vv, RealScalar.ONE))
      return RealScalar.ONE;
    else if (Scalars.lessThan(vv, RealScalar.of(2)))
      return RealScalar.of(-2);
    else if (Scalars.lessThan(vv, RealScalar.of(2)))
      return RealScalar.ONE;
    else
      return RealScalar.ZERO;
  }
  
  public static Tensor getBasisVector(int n, Scalar x, int der) {
    x = Max.of(x, RealScalar.ZERO);
    x = Min.of(x, RealScalar.of(n - 2));
    final Scalar xx = x;
    if(der == 0)
      return Tensors.vector((i) -> getBasisElement(i, xx), n);
    else if(der == 1)
      return Tensors.vector((i) -> getBasisElement1Der(i, xx), n);
    else if(der == 2)
      return Tensors.vector((i) -> getBasisElement2Der(i, xx), n);
    else
      return null;
  }
  
  public static Tensor getBasisMatrix(int n, Tensor queryPositions, int der) {
    final Tensor queryPositionsFinal = queryPositions;
    return Tensors.vector((i)->getBasisVector(n, queryPositionsFinal.Get(i), der), queryPositions.length());
  }
}
