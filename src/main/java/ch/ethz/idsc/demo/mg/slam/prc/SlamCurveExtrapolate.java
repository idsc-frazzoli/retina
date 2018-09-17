// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** extrapolates a curve estimated by the SLAM algorithm */
/* package */ class SlamCurveExtrapolate extends AbstractSlamCurveStep {
  private final SlamCurvatureFilter slamCurvatureFilter;
  private final SlamHeadingFilter slamHeadingFilter;
  private final Scalar numberOfPoints;
  private final Scalar curveFactor;
  private final Scalar extrapolationDistance;

  SlamCurveExtrapolate(SlamPrcContainer slamCurveContainer) {
    super(slamCurveContainer);
    slamCurvatureFilter = new SlamCurvatureFilter();
    slamHeadingFilter = new SlamHeadingFilter();
    numberOfPoints = SlamPrcConfig.GLOBAL.numberOfPoints;
    curveFactor = SlamPrcConfig.GLOBAL.curveFactor;
    extrapolationDistance = SlamPrcConfig.GLOBAL.extrapolationDistance;
  }

  @Override // from CurveListener
  public void process() {
    Tensor interpolatedCurve = slamPrcContainer.getInterpolatedCurve();
    Scalar localCurvature = slamCurvatureFilter.filterCurvature(interpolatedCurve);
    localCurvature = localCurvature.multiply(curveFactor);
    if (interpolatedCurve.length() > 2) {
      Tensor endPose = slamHeadingFilter.filterHeading(interpolatedCurve);
      Tensor extrapolatedCurve = SlamCurveExtrapolateUtil.extrapolateCurve(endPose, localCurvature, //
          extrapolationDistance, numberOfPoints);
      SlamCurveUtil.appendCurve(interpolatedCurve, extrapolatedCurve);
    }
    slamPrcContainer.setCurve(interpolatedCurve);
  }
}
