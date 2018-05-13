// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

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
  private static Scalar unitConversion;
  private static Tensor principalPoint; // [pixel]
  private static Tensor radDistortion; // [-] radial distortion with two coefficients is assumed
  private static Tensor focalLength; // [mm]
  private static Tensor transformationMatrix; // transforms homogeneous image coordinates into homogeneous physical coordinates
  private static boolean isInitialized;

  // TODO inelegant
  public static void initialize(PipelineConfig pipelineConfig) {
    Tensor inputTensor = ResourceData.of(pipelineConfig.calibrationFileName.toString());
    transformationMatrix = inputTensor.extract(0, 3);
    principalPoint = inputTensor.extract(3, 4);
    radDistortion = inputTensor.extract(4, 5);
    focalLength = inputTensor.extract(5, 6);
    unitConversion = pipelineConfig.unitConversion;
    isInitialized = true;
  }

  /**
   * 
   * @param imagePosX [pixel]
   * @param imagePosY [pixel]
   * @return physicalCoordinates [m] in gokart reference frame 
   */
  public static double[] imageToWorld(float imagePosX, float imagePosY) {
    if (!isInitialized)
      System.out.println("Uninitialized transformation!");
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
    // Transform to gokart front axle. NOTE unit is mm
    // TODO standardize calibration setup such that values are constant and load them through pipelineConfig
    physicalCoord = physicalCoord.add(Tensors.fromString("{{-370 , 1000, 0}}"));
    // TODO Transformation to gokart rear axle
    // ..
    // convert from [mm] to [m]
    physicalCoord = physicalCoord.divide(unitConversion);
    // note: x/y axis are inverse between gokart reference system and calibration reference system
    double[] physicalPos = { physicalCoord.get(0).Get(1).number().doubleValue(), physicalCoord.get(0).Get(0).number().doubleValue() };
    return physicalPos;
  }
}
