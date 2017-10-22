// code by jph
package ch.ethz.idsc.retina.alg.slam;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.MatrixExp;

public enum Se2Exp {
  ;
  /** maps an element (x, y, t) of se2 in standard coordinates
   * [0 -t x]
   * [t 0 y]
   * [0 0 0]
   * that is close to (0, 0, 0)
   * to the corresponding element in SE2.
   * 
   * @param x
   * @param y
   * @param theta
   * @return */
  public static Tensor of(Scalar x, Scalar y, Scalar theta) {
    // TODO matrix has analytic expression
    Tensor matrix = Array.zeros(3, 3);
    matrix.set(theta, 1, 0);
    matrix.set(theta.negate(), 0, 1);
    matrix.set(x, 0, 2);
    matrix.set(y, 1, 2);
    return MatrixExp.of(matrix);
  }

  public static Tensor of(Number x, Number y, Number theta) {
    return of(RealScalar.of(x), RealScalar.of(y), RealScalar.of(theta));
  }
}
