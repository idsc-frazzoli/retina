// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Eigensystem;

enum StaticHelper {
  ;
  /** @return eigenvectors of the symmetric covariance matrix - not necessarily scaled to unit length */
  static float[][] getEigenVectors(double[][] covariance) {
    Tensor covarianceMatrix = Tensors.matrixDouble(covariance);
    Eigensystem eigensystem = Eigensystem.ofSymmetric(covarianceMatrix);
    float[][] eigenVectors = new float[2][2];
    eigenVectors[0][0] = eigensystem.vectors().Get(0, 0).number().floatValue();
    eigenVectors[1][0] = eigensystem.vectors().Get(0, 1).number().floatValue();
    eigenVectors[0][1] = eigensystem.vectors().Get(1, 0).number().floatValue();
    eigenVectors[1][1] = eigensystem.vectors().Get(1, 1).number().floatValue();
    return eigenVectors;
  }
}
