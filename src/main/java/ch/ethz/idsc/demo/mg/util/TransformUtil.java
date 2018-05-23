// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

// Transformation between image and physical space. For documentation, see MATLAB single camera calibration.
// The CSV file must have the structure as below. Also important, exponential format must use capitalized E ("%E" in MATLAB).
// 1st-3rd lines represent the transformation matrix
// 4th line represents image coordinates of principal point [pixel]
// 5th line represents radial distortion coefficients [-]
// 6th line represents focal lengths [mm]
public class TransformUtil {
  private final Scalar unitConversion;
  private final Tensor principalPoint; // [pixel]
  private final Tensor radDistortion; // [-] radial distortion with two coefficients is assumed
  private final Tensor focalLength; // [mm]
  private final Tensor transformationMatrix; // transforms homogeneous image coordinates into homogeneous physical coordinates

  public TransformUtil(PipelineConfig pipelineConfig) {
    Tensor inputTensor = ResourceData.of(pipelineConfig.calibrationFileName.toString());
    transformationMatrix = inputTensor.extract(0, 3);
    principalPoint = inputTensor.extract(3, 4);
    radDistortion = inputTensor.extract(4, 5);
    focalLength = inputTensor.extract(5, 6);
    unitConversion = pipelineConfig.unitConversion;
  }

  /** @param imagePosX [pixel]
   * @param imagePosY [pixel]
   * @return physicalCoordinates [m] in gokart reference frame */
  public double[] imageToWorld(float imagePosX, float imagePosY) {
    Tensor normalizedImgCoord;
    Tensor undistortedImgCoord;
    Tensor physicalCoord;
    Scalar radDistSqr;
    // normalize image coordinates
    normalizedImgCoord = Tensors.matrixDouble(new double[][] { { imagePosX, imagePosY } }).subtract(principalPoint).pmul(focalLength.map(Scalar::reciprocal));
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
    // Transform to gokart rear axle. NOTE unit is mm
    // TODO these are magic constants but will not change often
    physicalCoord = physicalCoord.add(Tensors.fromString("{{-420 , 2200, 0}}"));
    // convert from [mm] to [m]
    physicalCoord = physicalCoord.divide(unitConversion);
    // note: x/y axis are inverse between gokart reference system and calibration reference system
    double[] physicalPos = { physicalCoord.get(0).Get(1).number().doubleValue(), physicalCoord.get(0).Get(0).number().doubleValue() };
    return physicalPos;
  }

  // for testing
  public static void main(String[] args) {
    TransformUtil test = new TransformUtil(new PipelineConfig());
    double[] physicalPos = test.imageToWorld(170, 100);
    System.out.println(physicalPos[0] + "/" + physicalPos[1]);
  }
}
