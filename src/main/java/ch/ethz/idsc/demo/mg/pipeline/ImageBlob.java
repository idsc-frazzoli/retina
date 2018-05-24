// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.Sqrt;

// this class provides a blob object in image coordinates
public class ImageBlob implements Serializable {
  private static final long serialVersionUID = 1L;
  private final float[] pos;
  private final double[][] covariance;
  private final int timeStamp; //
  private boolean isRecognized;
  private boolean isHidden;

  /** @param pos array of length 2
   * @param covariance array of size 2 x 2
   * @param timeStamp
   * @param isHidden */
  public ImageBlob(float[] pos, double[][] covariance, int timeStamp, boolean isHidden) {
    this.pos = pos;
    this.covariance = covariance;
    this.timeStamp = timeStamp;
    this.isHidden = isHidden;
  }

  // returns the square roots of the eigenvalues of the covariance matrix
  public float[] getStandardDeviation() {
    // float[] standardDeviation = new float[2];
    Tensor covarianceMatrix = Tensors.matrixDouble(getCovariance());
    Tensor stD = Sqrt.of(Eigensystem.ofSymmetric(covarianceMatrix).values());
    return Primitives.toFloatArray(stD);
    // standardDeviation[0] = stD.Get(0).number().floatValue();
    // standardDeviation[1] = stD.Get(1).number().floatValue();
    // return standardDeviation;
  }

  // returns the eigenvectors of the covariance matrix - not necessarily scaled to unit length
  public float[][] getEigenVectors() {
    float[][] eigenVectors = new float[2][2];
    Tensor covarianceMatrix = Tensors.matrixDouble(getCovariance());
    Eigensystem eigensystem = Eigensystem.ofSymmetric(covarianceMatrix);
    eigenVectors[0][0] = eigensystem.vectors().Get(0, 0).number().floatValue();
    eigenVectors[1][0] = eigensystem.vectors().Get(0, 1).number().floatValue();
    eigenVectors[0][1] = eigensystem.vectors().Get(1, 0).number().floatValue();
    eigenVectors[1][1] = eigensystem.vectors().Get(1, 1).number().floatValue();
    return eigenVectors;
  }

  // returns the angle between the eigenvector belonging to the first eigenvalue and the x-axis
  // TODO calling this function is inefficient if eigendecomp has already been carried out
  public double getRotAngle() {
    float[][] eigenVec = getEigenVectors();
    return Math.atan2(eigenVec[1][0], eigenVec[0][0]);
  }

  public float getDistanceTo(ImageBlob blob) {
    return (float) Math.hypot( //
        pos[0] - blob.getPos()[0], //
        pos[1] - blob.getPos()[1]);
  }

  public float[] getPos() {
    return pos;
  }

  public double[][] getCovariance() {
    return covariance;
  }

  public boolean getIsRecognized() {
    return isRecognized;
  }

  public boolean getIsHidden() {
    return isHidden;
  }

  public int getTimeStamp() {
    return timeStamp;
  }

  // scales the eigenvalues of the covariance matrix
  // TODO implement for the handlabeler
  public void setEigenValues(float first, float second) {
    // ...
  }

  // rotates the covariance matrix
  // TODO implement for the handlabeler
  public void setRotAngle(float rotAngle) {
    // ...
  }

  // required for handlabeling
  public void setPos(float[] pos) {
    this.pos[0] = pos[0];
    this.pos[1] = pos[1];
  }

  // required for handlabeling
  public void setCovariance(double[][] covariance) {
    this.covariance[0][0] = covariance[0][0];
    this.covariance[0][1] = covariance[0][1];
    this.covariance[1][0] = covariance[1][0];
    this.covariance[1][1] = covariance[1][1];
  }

  public void setIsRecognized(boolean isRecognized) {
    this.isRecognized = isRecognized;
  }
}
