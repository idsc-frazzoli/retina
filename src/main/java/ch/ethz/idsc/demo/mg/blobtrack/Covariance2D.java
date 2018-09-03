// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.Sqrt;

public class Covariance2D {
  private double[][] covariance;
  private Eigensystem eigensystem;

  Covariance2D(double[][] covariance) {
    this.covariance = covariance;
  }

  public void setCovariance(double firstAxis, double secondAxis, double rotAngle) {
    Tensor notRotated = DiagonalMatrix.of(firstAxis, secondAxis);
    Tensor rotMatrix = RotationMatrix.of(RealScalar.of(rotAngle));
    Tensor rotated = rotMatrix.dot(notRotated).dot(Transpose.of(rotMatrix));
    covariance = Primitives.toDoubleArray2D(rotated);
    covariance[1][0] = covariance[0][1]; // necessary because EigenSystem.ofSymmetric(..) requires a symmetric matrix
    Tensor covarianceMatrix = Tensors.matrixDouble(covariance);
    eigensystem = Eigensystem.ofSymmetric(covarianceMatrix);
  }

  public double[][] getCovariance() {
    return covariance;
  }

  /** @return angle between the eigenvector belonging to the first eigenvalue and the x-axis */
  public double rotAngle() {
    double yCoord = eigensystem.vectors().Get(0, 1).number().doubleValue();
    double xCoord = eigensystem.vectors().Get(0, 0).number().doubleValue();
    return Math.atan2(yCoord, xCoord);
  }

  public float[] stdDev() {
    Tensor stD = Sqrt.of(eigensystem.values());
    return Primitives.toFloatArray(stD);
  }
}
