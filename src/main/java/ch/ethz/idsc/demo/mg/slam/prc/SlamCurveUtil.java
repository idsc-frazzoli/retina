// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import ch.ethz.idsc.owl.math.planar.SignedCurvature2D;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.ArcTan;

// SLAM estimated curve utils
/* package */ enum SlamCurveUtil {
  ;
  // constant is equal to maximum path curvature that the vehicle can drive
  private static final Scalar MAX_PATH_CURVATURE = RealScalar.of(SteerConfig.GLOBAL.turningRatioMax.number().doubleValue());

  /** computes curvature. Curvature at first and last point cannot be computed and is set to the
   * neighboring value
   * 
   * @param curve assumed to not have identical points, minimum length 3
   * @return curvature Tensor of same length as curve */
  public static Tensor localCurvature(Tensor curve) {
    Tensor prev = curve.get(0);
    Tensor current = curve.get(1);
    Tensor curvature = Tensors.vector(0);
    for (int i = 2; i < curve.length(); ++i) {
      Tensor next = curve.get(i);
      // since curve is interpolated, we know that the 3 points are different from each other
      Tensor localCurvature = limitCurvature(SignedCurvature2D.of(prev, current, next).get());
      curvature.append(localCurvature);
      prev = current;
      current = next;
    }
    curvature.append(curvature.get(curvature.length() - 1));
    curvature.set(curvature.get(1), 0);
    return curvature;
  }

  /** @param curvature
   * @return curvature limited by maximum physically possible values */
  public static Scalar limitCurvature(Scalar curvature) {
    if (Scalars.lessEquals(curvature, MAX_PATH_CURVATURE.negate()))
      return MAX_PATH_CURVATURE.negate();
    if (Scalars.lessEquals(MAX_PATH_CURVATURE, curvature))
      return MAX_PATH_CURVATURE;
    return curvature;
  }

  /** @param curve
   * @return pose of last point of curve looking in tangent direction */
  public static Tensor getEndPose(Tensor curve) {
    Tensor endHeading = getEndHeading(curve);
    Tensor endPose = curve.get(curve.length() - 1).append(endHeading);
    return endPose;
  }

  // minimum curve length two
  public static Scalar getEndHeading(Tensor curve) {
    Tensor direction = curve.get(curve.length() - 1).subtract(curve.get(curve.length() - 2));
    return ArcTan.of(direction.Get(0), direction.Get(1));
  }

  /** @param firstCurve
   * @param secondCurve to be appended to first argument */
  public static void appendCurve(Tensor firstCurve, Tensor secondCurve) {
    secondCurve.stream() //
        .forEach(firstCurve::append);
  }
}
