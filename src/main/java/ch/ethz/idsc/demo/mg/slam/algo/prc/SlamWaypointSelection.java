// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Mean;

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
    Tensor featurePoints = SlamWaypointSelectionUtil.selectWaypoints( //
        worldWaypoints, slamContainer, //
        visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
    Tensor curve = SlamCurveInterpolate.refineFeaturePoints(featurePoints);
    if (curve.length() >= 6) {
      Scalar localCurvature = (Scalar) Mean.of(SlamCurveUtil.localCurvature(curve.extract(curve.length() - 6, curve.length())));
      Scalar endHeading = SlamCurveExtrapolate.getEndHeading(curve);
      slamCurvatureObserver.initialize(endHeading);
      localCurvature = slamCurvatureObserver.getAvgCurvature(localCurvature);
      endHeading = slamCurvatureObserver.getAvgHeading(endHeading);
      Tensor extrapolatedCurve = SlamCurveExtrapolate.extrapolateCurve(curve, localCurvature, //
          slamConfig.curveFactor, slamConfig.extrapolationDistance, numberOfPoints);
      extrapolatedCurve.stream() //
          .forEach(curve::append);
      slamContainer.setCurve(curve);
    }
  }
}
