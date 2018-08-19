// code by mg
package ch.ethz.idsc.demo.mg.blobtrack;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.Eigensystem;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** blob object for blob tracking algorithm. position in the image plane is tracked */
public class ImageBlob implements Serializable {
  private final float[] pos;
  private final int timeStamp;
  private final int blobID; // == 0 for hidden blobs
  private double[][] covariance;
  private boolean isRecognized;
  private boolean isHidden;

  /** @param pos array of length 2
   * @param covariance array of size 2 x 2
   * @param timeStamp
   * @param isHidden */
  public ImageBlob(float[] pos, double[][] covariance, int timeStamp, boolean isHidden, int blobID) {
    this.pos = pos;
    this.covariance = covariance;
    this.timeStamp = timeStamp;
    this.blobID = blobID;
    this.isHidden = isHidden;
  }

  /** @return square roots of the eigenvalues of the covariance matrix */
  public float[] getStandardDeviation() {
    Tensor covarianceMatrix = Tensors.matrixDouble(getCovariance());
    Tensor stD = Sqrt.of(Eigensystem.ofSymmetric(covarianceMatrix).values());
    return Primitives.toFloatArray(stD);
  }

  /** @return angle between the eigenvector belonging to the first eigenvalue and the x-axis */
  public double getRotAngle() {
    float[][] eigenVec = StaticHelper.getEigenVectors(getCovariance());
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

  public int getBlobID() {
    return blobID;
  }

  public void setPos(float[] pos) {
    this.pos[0] = pos[0];
    this.pos[1] = pos[1];
  }

  public void setCovariance(double firstAxis, double secondAxis, double rotAngle) {
    Tensor notRotated = DiagonalMatrix.of(firstAxis, secondAxis);
    Tensor rotMatrix = RotationMatrix.of(RealScalar.of(rotAngle));
    Tensor rotated = rotMatrix.dot(notRotated).dot(Transpose.of(rotMatrix));
    covariance = Primitives.toDoubleArray2D(rotated);
    covariance[1][0] = covariance[0][1]; // necessary because EigenSystem.ofSymmetric(..) requires a symmetric matrix
  }

  public void setIsRecognized(boolean isRecognized) {
    this.isRecognized = isRecognized;
  }
}
