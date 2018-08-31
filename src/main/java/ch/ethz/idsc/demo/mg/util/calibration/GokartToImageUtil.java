// code by mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.demo.mg.DavisConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** Transformation from physical space to image coordinates. For documentation, see MATLAB single camera calibration.
 * The CSV file must have the structure as below. Also important, exponential format must use capitalized E ("%E" in MATLAB).
 * 1st-3rd lines represent the transformation matrix
 * 4th line represents image coordinates of principal point [pixel]
 * 5th line represents radial distortion coefficients [-]
 * 6th line represents focal lengths [mm] */
// TODO z coordinate is implicitly set to zero
public class GokartToImageUtil implements GokartToImageInterface {
  private static final Tensor OFFSET = Tensors.vector(-420, 2200, 0);

  /** @param inputTensor of the form {transformationMatrix, principal point, radDistortion, focalLength}
   * @param unitConversion */
  public static GokartToImageUtil fromMatrix(Tensor inputTensor, Scalar unitConversion) {
    return new GokartToImageUtil(inputTensor, unitConversion);
  }

  // ---
  private final Scalar unitConversion;
  // ** transforms homogeneous physical coordinates into homogeneous image coordinates */
  private final Tensor transformationMatrix; // inverse of transformationMatrix in ImageToWorldUtil
  private final Tensor principalPoint; // [pixel]
  /** [-]radial distortion with two coefficients is assumed
   * but we build the coefficients to evaluate as quadratic polynomial of the form
   * {1, rd0, rd1}
   * for ordering of coefficient see {@link Multinomial#horner(Tensor, Scalar)} */
  private final ScalarUnaryOperator radDistortionPoly; //
  private final Tensor focalLength; // [mm]
  private final Tensor focalLengthInv; // [mm]

  GokartToImageUtil(Tensor inputTensor, Scalar unitConversion) {
    this.unitConversion = unitConversion;
    transformationMatrix = Inverse.of(inputTensor.extract(0, 3));
    principalPoint = inputTensor.get(3); // vector of length 2
    radDistortionPoly = Series.of(Join.of(Tensors.vector(1.0), inputTensor.get(4)));
    focalLength = inputTensor.get(5);
    focalLengthInv = focalLength.map(Scalar::reciprocal);
  }

  @Override
  public double[] gokartToImage(double worldPosX, double worldPosY) {
    return Primitives.toDoubleArray(gokartToImageTensor(worldPosX, worldPosY));
  }

  @Override
  public Tensor gokartToImage(Tensor gokartPos) {
    return gokartToImageTensor(gokartPos.Get(0).number().doubleValue(), gokartPos.Get(1).number().doubleValue());
  }

  public Tensor gokartToImageTensor(double worldPosX, double worldPosY) {
    // transform axes from go kart coordinate system to camera calibration system
    double cameraPhysicalX = worldPosY;
    double cameraPhysicalY = worldPosX;
    // unit conversion
    Tensor physicalCoord = Tensors.vector(cameraPhysicalX, cameraPhysicalY).multiply(unitConversion);
    // form homogeneous coordinates
    Tensor physicalHomgCoord = physicalCoord.append(RealScalar.ONE);
    // shift to camera coordinate system origin
    physicalHomgCoord = physicalHomgCoord.subtract(OFFSET);
    // transform to image plane
    Tensor rawImgCoord = physicalHomgCoord.dot(transformationMatrix);
    // enforce homogeneous coordinates
    rawImgCoord = rawImgCoord.divide(rawImgCoord.Get(2));
    // normalize image coordinates
    Tensor normalizedImgCoord = rawImgCoord.extract(0, 2).subtract(principalPoint).pmul(focalLengthInv);
    // calculate radial distance
    Scalar radDistSqr = Norm2Squared.ofVector(normalizedImgCoord);
    // calculate distortion coefficient
    Scalar distortionCoeff = radDistortionPoly.apply(radDistSqr);
    // compute distorted normalized image coordinates
    Tensor distortedImgCoord = normalizedImgCoord.multiply(distortionCoeff);
    // unnormalize distorted image coordinates
    return distortedImgCoord.pmul(focalLength).add(principalPoint);
  }

  // testing
  public static void main(String[] args) {
    GokartToImageUtil test = new DavisConfig().createGokartToImageUtil();
    double[] imgPos = test.gokartToImage(3.4386292832405725, -0.4673008409796591);
    System.out.println(imgPos[0] + " , " + imgPos[1]);
  }
}
