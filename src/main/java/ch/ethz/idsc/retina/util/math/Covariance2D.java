// code by mg, jph
package ch.ethz.idsc.retina.util.math;

import java.io.Serializable;

import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.red.Times;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** immutable */
public class Covariance2D implements Serializable {
  /** @param firstAxis
   * @param secondAxis
   * @param angle
   * @return */
  public static Covariance2D of(Scalar firstAxis, Scalar secondAxis, Scalar angle) {
    Tensor matrix = matrix(firstAxis, secondAxis, angle);
    matrix.set(matrix.get(0, 1), 1, 0);
    return new Covariance2D(matrix);
  }

  /** @param a
   * @param b
   * @param angle
   * @return RotationMatrix[angle].DiagonalMatrix[{a, b}].RotationMatrix[-angle] */
  public static Tensor matrix(Scalar a, Scalar b, Scalar angle) {
    Scalar c = Cos.FUNCTION.apply(angle);
    Scalar s = Sin.FUNCTION.apply(angle);
    Scalar c2 = c.multiply(c);
    Scalar s2 = s.multiply(s);
    Scalar dg = Times.of(a.subtract(b), c, s);
    return Tensors.matrix(new Scalar[][] { //
        { a.multiply(c2).add(b.multiply(s2)), dg }, //
        { dg, b.multiply(c2).add(a.multiply(s2)) } });
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
    int index = ArgMax.of(eigensystem.values().map(Scalar::abs));
    return ArcTan2D.of(eigensystem.vectors().get(index));
  }

  /** @return vector of length 2 consisting of eigenvalues */
  public Tensor stdDev() {
    return Sqrt.of(eigensystem.values());
  }

  /* package */ Eigensystem eigensystem() {
    return eigensystem;
  }
}
