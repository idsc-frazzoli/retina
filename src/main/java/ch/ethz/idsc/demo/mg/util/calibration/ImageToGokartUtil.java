// code by mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Transformation between image and physical space. For documentation, see MATLAB single camera calibration.
 * The CSV file must have the structure as below. Exponential format must use capitalized E ("%E" in MATLAB).
 * 1st-3rd lines represent the transformation matrix
 * 4th line represents image coordinates of principal point [pixel]
 * 5th line represents radial distortion coefficients [-]
 * 6th line represents focal lengths [mm] */
public class ImageToGokartUtil implements ImageToGokartInterface {
  /** Offset between origin of calibration frame and origin of go kart frame. Unit is [mm] */
  private static final Tensor OFFSET = Tensors.vector(-420, 2200, 0);

  /** @param inputTensor of the form {transformationMatrix, principal point, radDistortion, focalLength}
   * @param unitConversion
   * @param width */
  public static ImageToGokartUtil fromMatrix(Tensor inputTensor, Scalar unitConversion, int width) {
    return new ImageToGokartUtil(inputTensor, unitConversion, width);
  }

  // ---
  private final Scalar unitConversion;
  private final int width;
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
  /* package */ ImageToGokartUtil(Tensor inputTensor, Scalar unitConversion, int width) {
    this.unitConversion = unitConversion;
    this.width = width;
    transformationMatrix = inputTensor.extract(0, 3);
    principalPoint = inputTensor.get(3); // vector of length 2
    radDistortionPoly = Series.of(Join.of(Tensors.vector(1.0), inputTensor.get(4)));
    focalLength = inputTensor.get(5);
    focalLengthInv = focalLength.map(Scalar::reciprocal);
  }

  // from ImageToGokartInterface
  @Override
  public double[] imageToGokart(int imagePosX, int imagePosY) {
    return Primitives.toDoubleArray(imageToGokartTensor(imagePosX, imagePosY));
  }

  public double[] imageToGokart(double imagePosX, double imagePosY) {
    return Primitives.toDoubleArray(imageToGokartTensor(imagePosX, imagePosY));
  }

  // from ImageToGokartInterface
  @Override
  public Tensor imageToGokartTensor(int index) {
    int imagePosY = index / width;
    int imagePosX = index % width;
    return imageToGokartTensor(imagePosX, imagePosY);
  }

  public Tensor imageToGokartTensor(double imagePosX, double imagePosY) {
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
    // Transform to go kart rear axle. NOTE unit is mm
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
