// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2CoveringGroupElement;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum SlamCurveExtrapolateUtil {
  ;
  /** extrapolates given endPose with a circle segment of tangent to pose
   * 
   * @param endPose
   * @param curvature curvature of circle segment
   * @param distance length of extrapolated circle segment
   * @param numberOfPoints number of points along extrapolated segment
   * @return extrapolatedCurve */
  public static Tensor extrapolateCurve(Tensor endPose, Scalar curvature, Scalar distance, Scalar numberOfPoints) {
    // starting point of extrapolation
    Tensor extrapolatedCurve = Tensors.of(endPose.extract(0, 2));
    // negate to extrapolate to inner side of curve
    curvature = curvature.negate();
    curvature = SlamCurveUtil.limitCurvature(curvature);
    Tensor circleParam = Tensors.vector(1, 0, curvature.number().doubleValue());
    Se2CoveringGroupElement se2CoveringGroupAction = new Se2CoveringGroupElement(endPose);
    Scalar stepSize = distance.divide(numberOfPoints);
    for (int i = 0; i < numberOfPoints.number().intValue(); ++i) {
      Tensor extrapolatedPoint = se2CoveringGroupAction.combine(Se2CoveringExponential.INSTANCE.exp(circleParam.multiply(stepSize.multiply(RealScalar.of(i)))));
      extrapolatedPoint = extrapolatedPoint.extract(0, 2);
      extrapolatedCurve.append(extrapolatedPoint);
    }
    return extrapolatedCurve;
  }
}
