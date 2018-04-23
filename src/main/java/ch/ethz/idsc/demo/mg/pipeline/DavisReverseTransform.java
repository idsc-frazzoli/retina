// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.StringScalar;
import ch.ethz.idsc.tensor.io.StringScalarQ;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

// transformation from image to physical space. For documentation, see MATLAB single camera calibration.
public class DavisReverseTransform {
  // fields
  private Tensor principalPoint;
  private Tensor radDistortion;
  private Tensor focalLength;
  private Tensor transformationMatrix;

  // TODO: import matrices as csv file from matlab
  DavisReverseTransform() {
    importCameraParams();
    TrackedBlob test = new TrackedBlob(new float[] { 95.888f, 87.4894f }, new double[][] { { 1, 0 }, { 0, 1 } }, 0, false);
    transformSingleBlob(test);
    // ...
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
//    Tensor normalizedImgCoord;
    double[][] normalizedImgCoord = new double[1][2];
    Tensor undistortedImgCoord;
    Tensor physicalCoord;
    double radDistance;
    // undistort image coordinates
    normalizedImgCoord[0][0] = (trackedBlob.getPos()[0] - principalPoint.get(0).Get(0).number().doubleValue())/focalLength.get(0).Get(0).number().doubleValue();
    normalizedImgCoord[0][1] = (trackedBlob.getPos()[1] - principalPoint.get(0).Get(1).number().doubleValue())/focalLength.get(0).Get(1).number().doubleValue();
    
//    normalizedImgCoord = Tensors.matrixDouble(new double[][] {{trackedBlob.getPos()[0]},{trackedBlob.getPos()[1]}}).subtract(principalPoint).Power.of;
    
    radDistance = Math.sqrt(normalizedImgCoord[0][0] * normalizedImgCoord[0][0] + normalizedImgCoord[0][1] * normalizedImgCoord[0][1]);
    
    double denominator = 1 + radDistance * radDistance * radDistortion.get(0).Get(0).number().doubleValue()
        + Math.pow(radDistance, 4) * radDistortion.get(0).Get(1).number().doubleValue();
    
    
    normalizedImgCoord[0][0] = normalizedImgCoord[0][0] / denominator;
    normalizedImgCoord[0][1] = normalizedImgCoord[0][1] / denominator;
    
//    normalizedImgCoord.divide(denominator);
//    undistortedImgCoord[0] = normalizedImgCoord[0]*focalLength.get(0).Get(0).number().doubleValue()+principalPoint.get(0).Get(0).number().doubleValue();
    undistortedImgCoord = Tensors.matrixDouble(normalizedImgCoord).pmul(focalLength).add(principalPoint);
    
    undistortedImgCoord = Join.of(1, undistortedImgCoord,IdentityMatrix.of(1));
    
    System.out.println(undistortedImgCoord);
    System.out.println(transformationMatrix);
    
//    physicalCoord = undistortedImgCoord.dot(transformationMatrix);
//    transformationMatrix.dot(undistortedImgCoord);
    undistortedImgCoord.dot(transformationMatrix);
//    System.out.println(physicalCoord);
    
    
    PhysicalBlob physicalBlob = new PhysicalBlob();
    return physicalBlob;
  }

  //TODO structure of csv file and name is hardcoded
  private void importCameraParams() {
    Tensor inputTensor = null;
    try {
      inputTensor = Import.of(UserHome.Pictures("test.csv"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    boolean isNummeric = StringScalarQ.any(inputTensor);
    System.out.println(isNummeric);
    transformationMatrix = inputTensor.extract(0, 3);
    principalPoint = inputTensor.extract(3, 4);
    radDistortion = inputTensor.extract(4, 5);
    focalLength = inputTensor.extract(5, 6);
  }

  public static void main(String[] args) {
    DavisReverseTransform test = new DavisReverseTransform();
  }
}
