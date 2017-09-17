// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.MatrixExp;

public enum Se2Sampler {
  ;
  public static Tensor get(Number theta, Number x, Number y) {
    return get(RealScalar.of(theta), RealScalar.of(x), RealScalar.of(y));
  }

  public static Tensor get(Scalar theta, Scalar x, Scalar y) {
    Tensor matrix = Array.zeros(3, 3);
    matrix.set(theta, 1, 0);
    matrix.set(theta.negate(), 0, 1);
    matrix.set(x, 0, 2);
    matrix.set(y, 1, 2);
    return MatrixExp.of(matrix);
  }
}
