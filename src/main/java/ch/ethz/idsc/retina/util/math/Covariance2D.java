// code by mg, jph
package ch.ethz.idsc.retina.util.math;

import java.io.Serializable;

import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** immutable */
public class Covariance2D implements Serializable {
  /** @param firstAxis
   * @param secondAxis
   * @param angle
   * @return */
  public static Covariance2D of(Scalar firstAxis, Scalar secondAxis, Scalar angle) {
    Tensor rotation = RotationMatrix.of(angle);
    Tensor diagonal = DiagonalMatrix.of(firstAxis, secondAxis);
    Tensor matrix = rotation.dot(diagonal).dot(Transpose.of(rotation));
    // TODO can speed up to
    // RotationMatrix[t].DiagonalMatrix[{a, b}].RotationMatrix[-t] ==
    // {{a Cos[t]^2 + b Sin[t]^2, (a - b) Cos[t] Sin[t]},
    // {(a - b) Cos[t] Sin[t], b Cos[t]^2 + a Sin[t]^2}}
    matrix.set(matrix.get(0, 1), 1, 0);
    return new Covariance2D(matrix);
  }

  // ---
  private final Tensor matrix;
  private final Eigensystem eigensystem;

  /** @param symmetric covariance matrix with dimensions 2 x 2 */
  public Covariance2D(Tensor matrix) {
    this.matrix = matrix;
    eigensystem = Eigensystem.ofSymmetric(matrix);
    if (eigensystem.values().length() != 2)
      throw TensorRuntimeException.of(matrix);
  }

  public Tensor matrix() {
    return matrix.unmodifiable();
  }

  /** @return angle between the eigenvector belonging to the first eigenvalue and the x-axis */
  public Scalar angle() {
    // TODO is this well-defined? shouldn't the max abs Eigenvalue be identified?
    // int index = ArgMax.of(eigensystem.values().map(Scalar::abs));
    return ArcTan2D.of(eigensystem.vectors().get(0));
  }

  /** @return vector of length 2 consisting of eigenvalues */
  public Tensor stdDev() {
    return Sqrt.of(eigensystem.values());
  }
}
