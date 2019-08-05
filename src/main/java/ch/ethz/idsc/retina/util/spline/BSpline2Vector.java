// code by mh
package ch.ethz.idsc.retina.util.spline;

import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** based on matlab code:
 * 
 * <pre>
 * function [xx,yy] = casadiDynamicBSPLINE(x,points)
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
 * end
 * </pre> */
// TODO JPH replace in some cases using bspline2 subdivision
public abstract class BSpline2Vector implements ScalarTensorFunction {
  private static final ScalarUnaryOperator[] BSPLINE2D = { //
      BSpline2D0.FUNCTION, //
      BSpline2D1.FUNCTION, //
      BSpline2D2.FUNCTION, //
      BSpline2Dn.FUNCTION //
  };

  /** @param n number of control points
   * @param derivative non-negative
   * @param cyclic
   * @return */
  public static ScalarTensorFunction of(int n, int derivative, boolean cyclic) {
    return cyclic //
        ? new CyclicBSpline2Vector(n, derivative)
        : new StringBSpline2Vector(n, derivative);
  }

  // ---
  final int n;
  final ScalarUnaryOperator bSpline2D;

  protected BSpline2Vector(int n, int derivative) {
    this.n = n;
    bSpline2D = BSPLINE2D[Math.min(derivative, 3)];
  }
  // Tensor queryPositions;
  // if (closed)
  // Tensors.vector(i -> RealScalar.of((n + 0.0) * (i / (m + 0.0))), m);
  // Tensors.vector(i -> RealScalar.of((n + 0.0) * (i / (m + 0.0))), m);
  // else
  // Tensors.vector(i -> RealScalar.of((n - 2.0) * (i / (m - 1.0))), m - 1);
  // Tensors.vector(i -> RealScalar.of((n - 2.0) * (i / (m - 1.0))), m);
  // final Tensor queryPositions;
  // if (closed) // we found closed solution
  // queryPositions =
  // else
  //
}
