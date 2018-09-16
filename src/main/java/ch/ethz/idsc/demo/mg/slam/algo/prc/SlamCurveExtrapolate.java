// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.owl.math.map.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Mean;

/** methods for curve extrapolation in SLAM algorithm */
/* package */ class SlamCurveExtrapolate extends AbstractSlamCurveStep {
  private final SlamCurvatureObserver slamCurvatureObserver;
  private final Scalar numberOfPoints;
  private final Scalar curveFactor;
  private final Scalar extrapolationDistance;

  SlamCurveExtrapolate(SlamCurveContainer slamCurveContainer) {
    super(slamCurveContainer);
    slamCurvatureObserver = new SlamCurvatureObserver();
    numberOfPoints = SlamPrcConfig.GLOBAL.numberOfPoints;
    curveFactor = SlamPrcConfig.GLOBAL.curveFactor;
    extrapolationDistance = SlamPrcConfig.GLOBAL.extrapolationDistance;
  }

  /** extrapolates last point of curve with a circle segment of curvature multiplied with curveFactor
   * 
   * @param curve
   * @param localCurvature curvature at the second last point of curve
   * @param curveFactor factor with which localcurvature is multiplied
   * @param distance length of extrapolated circle segment
   * @param numberOfPoints number of points along extrapolated segment
   * @return extrapolatedCurve */
  public static Tensor extrapolateCurve(Tensor curve, Scalar localCurvature, Scalar curveFactor, Scalar distance, Scalar numberOfPoints) {
    Tensor endPose = SlamCurveUtil.getEndPose(curve);
    Tensor extrapolatedCurve = Tensors.of(endPose.extract(0, 2));
    // negate to extrapolate to inner side of curve
    localCurvature = localCurvature.multiply(curveFactor).negate();
    localCurvature = SlamCurveUtil.limitCurvature(localCurvature);
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

  @Override // from CurveListener
  public void process() {
    Tensor curve = slamCurveContainer.getInterpolated();
    if (curve.length() >= 6) {
      Scalar localCurvature = (Scalar) Mean.of(SlamCurveUtil.localCurvature(curve.extract(curve.length() - 6, curve.length())));
      Scalar endHeading = SlamCurveUtil.getEndHeading(curve);
      slamCurvatureObserver.initialize(endHeading);
      localCurvature = slamCurvatureObserver.getAvgCurvature(localCurvature);
      endHeading = slamCurvatureObserver.getAvgHeading(endHeading);
      Tensor extrapolatedCurve = SlamCurveExtrapolate.extrapolateCurve(curve, localCurvature, //
          curveFactor, extrapolationDistance, numberOfPoints);
      extrapolatedCurve.stream() //
          .forEach(curve::append);
      slamCurveContainer.setCurve(curve);
    }
  }
}
