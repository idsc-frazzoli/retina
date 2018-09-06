// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** selects the way point that should be followed by the pure pursuit algorithm */
/* package */ class SlamWaypointSelection implements WorldWaypointListener {
  private final SlamContainer slamContainer;
  private final double visibleBoxXMin;
  private final double visibleBoxXMax;
  private final double visibleBoxHalfWidth;

  protected SlamWaypointSelection(SlamContainer slamContainer, SlamConfig slamConfig) {
    this.slamContainer = slamContainer;
    visibleBoxXMin = Magnitude.METER.toDouble(slamConfig.visibleBoxXMin);
    visibleBoxXMax = Magnitude.METER.toDouble(slamConfig.visibleBoxXMax);
    visibleBoxHalfWidth = Magnitude.METER.toDouble(slamConfig.visibleBoxHalfWidth);
  }

  @Override // from WorldWaypointListener
  public void worldWaypoints(List<double[]> worldWaypoints) {
    SlamWaypointSelectionUtil.getWaypoints( //
        worldWaypoints, slamContainer, //
        visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
  }
}
