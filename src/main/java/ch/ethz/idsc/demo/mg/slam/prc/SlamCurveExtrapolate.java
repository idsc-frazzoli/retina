// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** extrapolates a curve estimated by the SLAM algorithm */
/* package */ class SlamCurveExtrapolate extends AbstractSlamCurveStep {
  private final SlamCurvatureSmoother slamCurvatureFilter;
  private final SlamHeadingSmoother slamHeadingFilter;
  private final Scalar numberOfPoints;
  private final Scalar curveFactor;
  private final Scalar extrapolationDistance;

  SlamCurveExtrapolate(SlamPrcContainer slamCurveContainer) {
    super(slamCurveContainer);
    slamCurvatureFilter = new SlamCurvatureSmoother();
    slamHeadingFilter = new SlamHeadingSmoother();
    numberOfPoints = SlamPrcConfig.GLOBAL.numberOfPoints;
    curveFactor = SlamPrcConfig.GLOBAL.curveFactor;
    extrapolationDistance = SlamPrcConfig.GLOBAL.extrapolationDistance;
  }

  @Override // from CurveListener
  public void process() {
    Tensor interpolatedCurve = slamPrcContainer.getInterpolatedCurve();
    Scalar localCurvature = slamCurvatureFilter.smoothCurvature(interpolatedCurve);
    localCurvature = localCurvature.multiply(curveFactor);
    if (interpolatedCurve.length() >= 3) {
      Tensor endPose = slamHeadingFilter.smoothHeading(interpolatedCurve);
      Tensor extrapolatedCurve = SlamCurveExtrapolateUtil.extrapolateCurve(endPose, localCurvature, //
          extrapolationDistance, numberOfPoints);
      appendCurve(interpolatedCurve, extrapolatedCurve);
      slamPrcContainer.setCurve(interpolatedCurve);
    }
  }

  /** @param firstCurve
   * @param secondCurve to be appended to first argument */
  private static void appendCurve(Tensor firstCurve, Tensor secondCurve) {
    secondCurve.stream().forEach(firstCurve::append);
  }
}
