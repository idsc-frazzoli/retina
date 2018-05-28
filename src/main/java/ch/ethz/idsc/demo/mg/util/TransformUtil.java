// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Multinomial;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Transformation between image and physical space. For documentation, see MATLAB single camera calibration.
 * The CSV file must have the structure as below. Also important, exponential format must use capitalized E ("%E" in MATLAB).
 * 1st-3rd lines represent the transformation matrix
 * 4th line represents image coordinates of principal point [pixel]
 * 5th line represents radial distortion coefficients [-]
 * 6th line represents focal lengths [mm] */
public class TransformUtil {
  // TODO these are magic constants but will not change often
  private static final Tensor OFFSET = Tensors.vector(-420, 2200, 0);

  /** @param inputTensor of the form {transformationMatrix, principal point, radDistortion, focalLength}
   * @param unitConversion */
  public static TransformUtil fromMatrix(Tensor inputTensor, Scalar unitConversion) {
    return new TransformUtil(inputTensor, unitConversion);
  }

  // ---
  private final Scalar unitConversion;
  /** transforms homogeneous image coordinates into homogeneous physical coordinates */
  private final Tensor transformationMatrix;
  private final Tensor principalPoint; // [pixel]
  /** [-]radial distortion with two coefficients is assumed
   * but we build the coefficients to evaluate as quadratic polynomial of the form
   * {1, rd0, rd1}
   * for ordering of coefficient see {@link Multinomial#horner(Tensor, Scalar)} */
  private final ScalarUnaryOperator radDistortionPoly; //
  private final Tensor focalLength; // [mm]
  private final Tensor focalLengthInv; // [mm]

  // constructor is private so that API can extend/be modified easier in the future if needed
  /* package */ TransformUtil(Tensor inputTensor, Scalar unitConversion) {
    this.unitConversion = unitConversion;
    transformationMatrix = inputTensor.extract(0, 3);
    principalPoint = inputTensor.get(3); // vector of length 2
    radDistortionPoly = new HornerScheme(Join.of(Tensors.vector(1.0), inputTensor.get(4)));
    focalLength = inputTensor.get(5);
    focalLengthInv = focalLength.map(Scalar::reciprocal);
  }

  /** @param imagePosX [pixel]
   * @param imagePosY [pixel]
   * @return physicalCoordinates [m] in gokart reference frame */
  public double[] imageToWorld(float imagePosX, float imagePosY) {
    return Primitives.toDoubleArray(imageToWorldTensor(imagePosX, imagePosY));
  }

  /** @param imagePosX [pixel]
   * @param imagePosY [pixel]
   * @return physicalCoordinates [m] in gokart reference frame */
  public Tensor imageToWorldTensor(float imagePosX, float imagePosY) {
    // normalize image coordinates
    Tensor normalizedImgCoord = Tensors.vector(imagePosX, imagePosY).subtract(principalPoint).pmul(focalLengthInv);
    // calculate squared radial distance
    Scalar radDistSqr = Norm2Squared.ofVector(normalizedImgCoord);
    // remove image distortion
    Scalar denominator = radDistortionPoly.apply(radDistSqr);
    normalizedImgCoord = normalizedImgCoord.divide(denominator);
    // revert normalization and form homogeneous coordinates
    Tensor undistortedImgCoord = //
        normalizedImgCoord.pmul(focalLength).add(principalPoint).append(RealScalar.ONE);
    // apply transformation
    Tensor physicalCoord = undistortedImgCoord.dot(transformationMatrix);
    // enforce homogeneous coordinates
    physicalCoord = physicalCoord.divide(physicalCoord.Get(2));
    // Transform to gokart rear axle. NOTE unit is mm
    physicalCoord = physicalCoord.add(OFFSET);
    // convert from [mm] to [m]
    physicalCoord = physicalCoord.divide(unitConversion);
    // note: x/y axis are inverse between gokart reference system and calibration reference system
    return Tensors.of(physicalCoord.Get(1), physicalCoord.Get(0));
  }

  public void printInfo() {
    System.out.println("transformationMatrix=" + Pretty.of(transformationMatrix));
    System.out.println("principalPoint=" + principalPoint);
    System.out.println("focalLength=" + focalLength);
  }
}
