// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** finds currently visible way points and computes lookAhead to be followed by the pure pursuit algorithm */
/* package */ class SlamWaypointSelection implements WorldWaypointListener {
  private final SlamContainer slamContainer;
  private final double visibleBoxXMin;
  private final double visibleBoxXMax;
  private final double visibleBoxHalfWidth;
  private final double offset;

  protected SlamWaypointSelection(SlamContainer slamContainer, SlamConfig slamConfig) {
    this.slamContainer = slamContainer;
    visibleBoxXMin = Magnitude.METER.toDouble(slamConfig.visibleBoxXMin);
    visibleBoxXMax = Magnitude.METER.toDouble(slamConfig.visibleBoxXMax);
    visibleBoxHalfWidth = Magnitude.METER.toDouble(slamConfig.visibleBoxHalfWidth);
    offset = Magnitude.METER.toDouble(slamConfig.offset);
  }

  @Override // from WorldWaypointListener
  public void worldWaypoints(List<double[]> worldWaypoints) {
    List<double[]> visibleWaypoints = SlamWaypointSelectionUtil.selectWaypoints( //
        worldWaypoints, slamContainer, //
        visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
    SlamLookAheadComputation.selectLookAhead(slamContainer, visibleWaypoints, offset);
    SlamCurveInterpolate.interpolateFeaturePoints(slamContainer, visibleWaypoints);
    // SlamCenterLineFinder.offSetCurve(slamContainer.getRefinedWaypointCurve().get(), Tensors.vector(0, 0));
  }
}
