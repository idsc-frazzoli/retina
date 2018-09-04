// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** selects the way point that should be followed by the pure pursuit algorithm */
// TODO instead of periodic, execute the task once map processing task is finished
/* package */ class SlamWaypointSelection extends PeriodicSlamStep implements WorldWaypointListener {
  private double visibleBoxXMin;
  private double visibleBoxXMax;
  private double visibleBoxHalfWidth;

  protected SlamWaypointSelection(SlamContainer slamContainer, SlamConfig slamConfig) {
    super(slamContainer, slamConfig.waypointSelectionUpdateRate);
    visibleBoxXMin = Magnitude.METER.toDouble(slamConfig.visibleBoxXMin);
    visibleBoxXMax = Magnitude.METER.toDouble(slamConfig.visibleBoxXMax);
    visibleBoxHalfWidth = Magnitude.METER.toDouble(slamConfig.visibleBoxHalfWidth);
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    SlamWaypointSelectionUtil.selectWaypoint(slamContainer);
  }

  @Override // from WorldWaypointListener
  public void worldWaypoints(List<double[]> worldWaypoints) {
    slamContainer.setWaypoints(SlamMapProcessingUtil.getWaypoints( //
        worldWaypoints, slamContainer.getPoseUnitless(), //
        visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth));
  }
}
