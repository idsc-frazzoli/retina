// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** finds currently visible way points and computes lookAhead to be followed by the pure pursuit algorithm */
/* package */ class SlamWaypointSelection implements WorldWaypointListener {
  private final SlamContainer slamContainer;
  private final SlamConfig slamConfig;
  private final SlamCurvatureObserver slamCurvatureObserver;
  private final Scalar numberOfPoints;
  private final double visibleBoxXMin;
  private final double visibleBoxXMax;
  private final double visibleBoxHalfWidth;

  SlamWaypointSelection(SlamContainer slamContainer, SlamConfig slamConfig) {
    this.slamContainer = slamContainer;
    this.slamConfig = slamConfig;
    slamCurvatureObserver = new SlamCurvatureObserver(slamConfig);
    visibleBoxXMin = Magnitude.METER.toDouble(slamConfig.visibleBoxXMin);
    visibleBoxXMax = Magnitude.METER.toDouble(slamConfig.visibleBoxXMax);
    visibleBoxHalfWidth = Magnitude.METER.toDouble(slamConfig.visibleBoxHalfWidth);
    numberOfPoints = slamConfig.numberOfPoints;
  }

  @Override // from WorldWaypointListener
  public void worldWaypoints(List<double[]> worldWaypoints) {
    List<double[]> featurePoints = SlamWaypointSelectionUtil.selectWaypoints( //
        worldWaypoints, slamContainer, //
        visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
    Tensor refinedFeaturePointCurve = SlamCurveInterpolate.refineFeaturePoints(featurePoints);
    if (refinedFeaturePointCurve.length() >= 3) {
      Scalar localCurvature = SlamCurveExtrapolate.getLocalCurvature(refinedFeaturePointCurve);
      Scalar endHeading = SlamCurveExtrapolate.getEndHeading(refinedFeaturePointCurve);
      slamCurvatureObserver.initialize(endHeading);
      if (slamCurvatureObserver.curvatureContinuous(localCurvature, endHeading)) {
        Tensor extrapolatedCurve = SlamCurveExtrapolate.extrapolateCurve(refinedFeaturePointCurve, localCurvature, //
            slamConfig.curveFactor, slamConfig.extrapolationDistance, numberOfPoints);
        extrapolatedCurve.stream() //
            .forEach(refinedFeaturePointCurve::append);
        slamContainer.setCurve(refinedFeaturePointCurve);
      }
    }
  }
}
