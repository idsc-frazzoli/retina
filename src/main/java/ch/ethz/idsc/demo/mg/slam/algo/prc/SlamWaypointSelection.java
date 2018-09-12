// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** finds currently visible way points and computes lookAhead to be followed by the pure pursuit algorithm */
/* package */ class SlamWaypointSelection implements WorldWaypointListener {
  private final SlamContainer slamContainer;
  private final Scalar extrapolationDistance;
  private final Scalar numberOfPoints;
  private final double visibleBoxXMin;
  private final double visibleBoxXMax;
  private final double visibleBoxHalfWidth;

  protected SlamWaypointSelection(SlamContainer slamContainer, SlamConfig slamConfig) {
    this.slamContainer = slamContainer;
    visibleBoxXMin = Magnitude.METER.toDouble(slamConfig.visibleBoxXMin);
    visibleBoxXMax = Magnitude.METER.toDouble(slamConfig.visibleBoxXMax);
    visibleBoxHalfWidth = Magnitude.METER.toDouble(slamConfig.visibleBoxHalfWidth);
    extrapolationDistance = slamConfig.extrapolationDistance;
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
      Scalar offset = SlamCenterLineFinder.computeOffset(refinedFeaturePointCurve, localCurvature);
      refinedFeaturePointCurve = SlamCenterLineFinder.constOffsetCurve(refinedFeaturePointCurve, offset);
      Tensor extrapolatedCurve = SlamCurveExtrapolate.extrapolateCurve(refinedFeaturePointCurve, localCurvature, extrapolationDistance, numberOfPoints);
      extrapolatedCurve.stream() //
          .forEach(refinedFeaturePointCurve::append);
    }
    slamContainer.setRefinedWaypointCurve(Optional.of(refinedFeaturePointCurve));
  }
}
