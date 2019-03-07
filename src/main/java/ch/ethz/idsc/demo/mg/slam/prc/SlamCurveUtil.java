// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.sophus.planar.SignedCurvature2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;

public enum SlamCurveUtil {
  ;
  /** curvature interval that the vehicle can drive */
  private static final Clip CURVATURE_CLIP = Clip.function( //
      Magnitude.PER_METER.toDouble(SteerConfig.GLOBAL.turningRatioMax.negate()), //
      Magnitude.PER_METER.toDouble(SteerConfig.GLOBAL.turningRatioMax));

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
      Tensor localCurvature = SignedCurvature2D.of(prev, current, next).get();
      curvature.append(localCurvature);
      prev = current;
      current = next;
    }
    curvature.append(curvature.get(curvature.length() - 1));
    curvature.set(curvature.get(1), 0);
    return curvature;
  }

  /** @param curvature without unit but with interpretation "rad*m^-1"
   * @return curvature limited by maximum physically possible values */
  public static Scalar limitCurvature(Scalar curvature) {
    return CURVATURE_CLIP.apply(curvature);
  }

  /** @param curve with minimum length 2
   * @return pose of last point of curve looking in tangent direction */
  public static Tensor getEndPose(Tensor curve) {
    Tensor endHeading = getEndHeading(curve);
    return curve.get(curve.length() - 1).append(endHeading);
  }

  /** @param curve minimum length 2
   * @return heading of tangent of curve endpoint */
  public static Scalar getEndHeading(Tensor curve) {
    Tensor direction = curve.get(curve.length() - 1).subtract(curve.get(curve.length() - 2));
    return ArcTan2D.of(direction);
  }
}
