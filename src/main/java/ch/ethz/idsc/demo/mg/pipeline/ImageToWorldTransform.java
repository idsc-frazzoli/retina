// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

// Transformation from image to physical space. For documentation, see MATLAB single camera calibration. Also, my master thesis.
// The CSV file must have the structure as below. Also important, exponential format must use capitalized E ("%E" in MATLAB).
// 1st-3rd lines represent the transformation matrix
// 4th line represents image coordinates of principal point [pixel]
// 5th line represents radial distortion coefficients [-]
// 6th line represents focal lengths [mm]
// TODO how to get path of current directory to load .CSV file?
// TODO still need to transform from checkerboard frame to gokart frame (once we try calibration on gokart)
public class ImageToWorldTransform {
  // fields
  // private String fileName = "test.csv"; // in demo.mg.pipeline
  private final int unitConversion = 1000; // [mm] to [m]
  private Tensor principalPoint; // [pixel]
  private Tensor radDistortion; // [-] radial distortion with two coeffcients is assumed
  private Tensor focalLength; // [mm]
  private Tensor transformationMatrix; // transforms homogeneous image coordinates into homogeneous physical coordinates
  private List<PhysicalBlob> physicalBlobs;

  ImageToWorldTransform() {
    physicalBlobs = new ArrayList<>();
    importCameraParams();
  }

  // main function which transforms list of TrackedBlobs to list of PhysicalBlobs.
  public void transformSelectedBlobs(List<ImageBlob> blobs) {
    List<PhysicalBlob> physicalBlobs = new ArrayList<>();
    for (int i = 0; i < blobs.size(); i++) {
      PhysicalBlob singlePhysicalBlob = transformSingleBlob(blobs.get(i));
      physicalBlobs.add(singlePhysicalBlob);
    }
    this.physicalBlobs = physicalBlobs;
  }

  public List<PhysicalBlob> getPhysicalBlobs() {
    return physicalBlobs;
  }

  private PhysicalBlob transformSingleBlob(ImageBlob imageBlob) {
    Tensor normalizedImgCoord;
    Tensor undistortedImgCoord;
    Tensor physicalCoord;
    Scalar radDistSqr;
    // normalize image coordinates
    normalizedImgCoord = Tensors.matrixDouble(new double[][] { { imageBlob.getPos()[0], imageBlob.getPos()[1] } }).subtract(principalPoint)
        .pmul(focalLength.map(Scalar::reciprocal));
    // calculate squared radial distance
    radDistSqr = (normalizedImgCoord.get(0).Get(0).multiply(normalizedImgCoord.get(0).Get(0)))
        .add(normalizedImgCoord.get(0).Get(1).multiply(normalizedImgCoord.get(0).Get(1)));
    // remove image distortion
    Scalar denominator = Scalars.fromString("1")
        .add(radDistSqr.multiply(radDistortion.get(0).Get(0)).add(radDistSqr.multiply(radDistSqr).multiply(radDistortion.get(0).Get(1))));
    normalizedImgCoord = normalizedImgCoord.divide(denominator);
    // revert normalization
    undistortedImgCoord = normalizedImgCoord.pmul(focalLength).add(principalPoint);
    // form homogeneous coordinates
    undistortedImgCoord = Join.of(1, undistortedImgCoord, IdentityMatrix.of(1));
    // apply transformation
    physicalCoord = undistortedImgCoord.dot(transformationMatrix);
    // enforce homogeneous coordinates
    physicalCoord = physicalCoord.divide(physicalCoord.get(0).Get(2));
    // here will be a further transformation into gokart frame which will require additional parameters
    PhysicalBlob physicalBlob = new PhysicalBlob(new double[] { physicalCoord.get(0).Get(0).number().doubleValue() / unitConversion,
        physicalCoord.get(0).Get(1).number().doubleValue() / unitConversion });
    return physicalBlob;
  }

  // imports parameters from CSV file that was generated with MATLAB
  private void importCameraParams() {
    Tensor inputTensor = ResourceData.of("/demo/mg/test.csv"); // notation for files in src/main/resources/...
    transformationMatrix = inputTensor.extract(0, 3);
    principalPoint = inputTensor.extract(3, 4);
    radDistortion = inputTensor.extract(4, 5);
    focalLength = inputTensor.extract(5, 6);
  }

  // for testing
  public static void main(String[] args) {
    ImageToWorldTransform test = new ImageToWorldTransform();
    ImageBlob imageBlob = new ImageBlob(new float[] { 169.3935f, 111.6323f }, new double[][] { { 1, 0 }, { 0, 1 } }, 0, false);
    PhysicalBlob physicalBlob = test.transformSingleBlob(imageBlob);
    System.out.println(physicalBlob.getPos()[0] + "/" + physicalBlob.getPos()[1]);
  }
}
