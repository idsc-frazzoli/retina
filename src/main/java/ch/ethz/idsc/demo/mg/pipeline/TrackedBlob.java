// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.Sqrt;

// this class provides a simple tracked blob object for visualization and pass to next modules
public class TrackedBlob implements Serializable {
  private static final long serialVersionUID = 1L;
  private final float[] pos;
  private final double[][] covariance;
  private final int timeStamp; //
  private boolean isCone;
  private boolean isHidden;

  /** @param pos array of length 2
   * @param covariance array of size 2 x 2
   * @param timeStamp
   * @param isHidden */
  public TrackedBlob(float[] pos, double[][] covariance, int timeStamp, boolean isHidden) {
    this.pos = pos;
    this.covariance = covariance;
    this.timeStamp = timeStamp;
    this.isHidden = isHidden;
  }

  // returns the square roots of the eigenvalues of the covariance matrix
  public float[] getStandardDeviation() {
    float[] standardDeviation = new float[2];
    Tensor covarianceMatrix = Tensors.matrixDouble(getCovariance());
    Tensor stD = Sqrt.of(Eigensystem.ofSymmetric(covarianceMatrix).values());
    standardDeviation[0] = stD.Get(0).number().floatValue();
    standardDeviation[1] = stD.Get(1).number().floatValue();
    return standardDeviation;
  }

  // returns the eigenvectors of the covariance matrix - not necessarily scaled to unit length
  public float[][] getEigenVectors() {
    float[][] eigenVectors = new float[2][2];
    Tensor covarianceMatrix = Tensors.matrixDouble(getCovariance());
    Eigensystem eigensystem = Eigensystem.ofSymmetric(covarianceMatrix);
    eigenVectors[0][0] = eigensystem.vectors().get(0).Get(0).number().floatValue();
    eigenVectors[1][0] = eigensystem.vectors().get(0).Get(1).number().floatValue();
    eigenVectors[0][1] = eigensystem.vectors().get(1).Get(0).number().floatValue();
    eigenVectors[1][1] = eigensystem.vectors().get(1).Get(1).number().floatValue();
    return eigenVectors;
  }

  // returns the angle between the eigenvector belonging to the first eigenvalue and the x-axis
  public double getRotAngle() {
    float[][] eigenVec = getEigenVectors();
    return Math.atan2(eigenVec[1][0], eigenVec[0][0]);
  }

  public float[] getPos() {
    return pos;
  }

  // required for handlabeling
  public void setPos(float[] pos) {
    this.pos[0] = pos[0];
    this.pos[1] = pos[1];
  }

  public double[][] getCovariance() {
    return covariance;
  }

  // required for handlabeling
  public void setCovariance(double[][] covariance) {
    this.covariance[0][0] = covariance[0][0];
    this.covariance[0][1] = covariance[0][1];
    this.covariance[1][0] = covariance[1][0];
    this.covariance[1][1] = covariance[1][1];
  }

  public int getTimeStamp() {
    return timeStamp;
  }

  public void setIsCone(boolean isCone) {
    this.isCone = isCone;
  }

  public boolean getIsCone() {
    return isCone;
  }

  public boolean getIsHidden() {
    return isHidden;
  }
}
