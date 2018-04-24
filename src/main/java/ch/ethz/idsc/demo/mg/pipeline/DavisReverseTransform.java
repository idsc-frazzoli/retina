// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

// transformation from image to physical space. For documentation, see MATLAB single camera calibration.
// the csv file must have the following structure:
// first 3 lines represent the transformation matrix
// 4th line represents coordinates of principal point
// 5th line represents radial distortion coefficients
// 6th line represents focal lengths
// TODO transformation is not yet completely using Tensor lib (find out how to use piecewise division)
public class DavisReverseTransform {
  // fields
  private Tensor principalPoint;
  private Tensor radDistortion;
  private Tensor focalLength;
  private Tensor transformationMatrix;

  DavisReverseTransform() {
    importCameraParams();
    TrackedBlob test = new TrackedBlob(new float[] { 95.888f, 87.4894f }, new double[][] { { 1, 0 }, { 0, 1 } }, 0, false);
    transformSingleBlob(test);
  }

  private List<PhysicalBlob> transformBlobs(List<TrackedBlob> blobs) {
    List<PhysicalBlob> physicalBlobs = new ArrayList<>();
    for (int i = 0; i < blobs.size(); i++) {
      PhysicalBlob singlePhysicalBlob = transformSingleBlob(blobs.get(i));
      physicalBlobs.add(singlePhysicalBlob);
    }
    return physicalBlobs;
  }

  private PhysicalBlob transformSingleBlob(TrackedBlob trackedBlob) {
    // Tensor normalizedImgCoord;
    double[][] normalizedImgCoord = new double[1][2];
    Tensor undistortedImgCoord;
    Tensor physicalCoord;
    double radDistSqr;
    // undistort image coordinates : this could be done with piecewise division
    normalizedImgCoord[0][0] = (trackedBlob.getPos()[0] - principalPoint.get(0).Get(0).number().doubleValue())
        / focalLength.get(0).Get(0).number().doubleValue();
    normalizedImgCoord[0][1] = (trackedBlob.getPos()[1] - principalPoint.get(0).Get(1).number().doubleValue())
        / focalLength.get(0).Get(1).number().doubleValue();
    // normalizedImgCoord = Tensors.matrixDouble(new double[][] {{trackedBlob.getPos()[0]},{trackedBlob.getPos()[1]}}).subtract(principalPoint).Power.of;
    radDistSqr = normalizedImgCoord[0][0] * normalizedImgCoord[0][0] + normalizedImgCoord[0][1] * normalizedImgCoord[0][1];
    double denominator = 1 + radDistSqr * radDistortion.get(0).Get(0).number().doubleValue()
        + Math.pow(radDistSqr, 2) * radDistortion.get(0).Get(1).number().doubleValue();
    normalizedImgCoord[0][0] = normalizedImgCoord[0][0] / denominator;
    normalizedImgCoord[0][1] = normalizedImgCoord[0][1] / denominator;
    // normalizedImgCoord.divide(denominator);
    undistortedImgCoord = Tensors.matrixDouble(normalizedImgCoord).pmul(focalLength).add(principalPoint);
    // form homogeneous coordinates
    undistortedImgCoord = Join.of(1, undistortedImgCoord, IdentityMatrix.of(1));
    // apply transformation
    physicalCoord = undistortedImgCoord.dot(transformationMatrix);
    // enforce homogeneous coordinates
    physicalCoord = physicalCoord.divide(physicalCoord.get(0).Get(2));
    PhysicalBlob physicalBlob = new PhysicalBlob();
    return physicalBlob;
  }

  // imports parameters from csv file that was generated with matlab
  private void importCameraParams() {
    Tensor inputTensor = null;
    try {
      inputTensor = Import.of(UserHome.Pictures("test.csv"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    transformationMatrix = inputTensor.extract(0, 3);
    principalPoint = inputTensor.extract(3, 4);
    radDistortion = inputTensor.extract(4, 5);
    focalLength = inputTensor.extract(5, 6);
  }

  // main function for testing
  public static void main(String[] args) {
    DavisReverseTransform test = new DavisReverseTransform();
  }
}
