// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** finds currently visible way points and computes lookAhead to be followed by the pure pursuit algorithm */
/* package */ class SlamWaypointSelection extends AbstractSlamCurveStep {
  private final double visibleBoxXMin;
  private final double visibleBoxXMax;
  private final double visibleBoxHalfWidth;

  SlamWaypointSelection(SlamCurveContainer slamCurveContainer) {
    super(slamCurveContainer);
    visibleBoxXMin = Magnitude.METER.toDouble(SlamPrcConfig.GLOBAL.visibleBoxXMin);
    visibleBoxXMax = Magnitude.METER.toDouble(SlamPrcConfig.GLOBAL.visibleBoxXMax);
    visibleBoxHalfWidth = Magnitude.METER.toDouble(SlamPrcConfig.GLOBAL.visibleBoxHalfWidth);
  }

  @Override // from CurveListener
  public void process() {
    Tensor worldWaypoints = slamCurveContainer.getWorldWaypoints();
    Tensor featurePoints = SlamWaypointSelectionUtil.selectWaypoints( //
        worldWaypoints, slamCurveContainer, //
        visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
    slamCurveContainer.setSelectedPoints(featurePoints);
  }
}
