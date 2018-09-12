// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.owl.math.map.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.owl.math.planar.SignedCurvature2D;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.ArcTan;

/** methods for curve extrapolation in SLAM algorithm */
/* package */ enum SlamCurveExtrapolate {
  ;
  // constant is equal to maximum path curvature that the vehicle can drive
  private static final Scalar MAX_PATH_CURVATURE = RealScalar.of(SteerConfig.GLOBAL.turningRatioMax.number().doubleValue());

  /** extrapolates last point of curve with a circle segment of corresponding curvature
   * 
   * @param curve
   * @param distance length of extrapolated circle segment
   * @param numberOfPoints number of points along extrapolated segment
   * @return extrapolatedCurve */
  public static Tensor extrapolateCurve(Tensor curve, Scalar localCurvature, Scalar distance, Scalar numberOfPoints) {
    Tensor endPose = getEndPose(curve);
    Tensor extrapolatedCurve = Tensors.of(endPose.extract(0, 2));
    localCurvature = localCurvature.negate();
    Tensor circleParam = Tensors.vector(1, 0, localCurvature.number().doubleValue());
    Se2CoveringGroupAction se2CoveringGroupAction = new Se2CoveringGroupAction(endPose);
    Scalar stepSize = distance.divide(numberOfPoints);
    for (int i = 0; i < numberOfPoints.number().intValue(); ++i) {
      Tensor extrapolatedPoint = se2CoveringGroupAction.combine(Se2CoveringExponential.INSTANCE.exp(circleParam.multiply(stepSize.multiply(RealScalar.of(i)))));
      extrapolatedPoint = extrapolatedPoint.extract(0, 2);
      extrapolatedCurve.append(extrapolatedPoint);
    }
    return extrapolatedCurve;
  }

  /** @param curve
   * @return pose of last point of curve looking in tangent direction */
  private static Tensor getEndPose(Tensor curve) {
    Tensor direction = curve.get(curve.length() - 1).subtract(curve.get(curve.length() - 2));
    Tensor endPose = curve.get(curve.length() - 1).append(ArcTan.of(direction.Get(0), direction.Get(1)));
    return endPose;
  }

  /** @param curve
   * @return curvature of second last point of curve, clipped by MAX_PATH_CURVATURE */
  public static Scalar getLocalCurvature(Tensor curve) {
    int curvaturePoint = curve.length() - 2;
    Tensor prev = curve.get(curvaturePoint - 1);
    Tensor current = curve.get(curvaturePoint);
    Tensor next = curve.get(curvaturePoint + 1);
    Scalar localCurvature = SignedCurvature2D.of(prev, current, next).get();
    if (Scalars.lessEquals(localCurvature, MAX_PATH_CURVATURE.negate()))
      return MAX_PATH_CURVATURE.negate();
    if (Scalars.lessEquals(MAX_PATH_CURVATURE, localCurvature))
      return MAX_PATH_CURVATURE;
    return localCurvature;
  }
}
