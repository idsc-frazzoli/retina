// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** extracts way points from a map using threshold operation,
 * morphological processing and connected component labeling */
/* package */ class SlamMapProcessing extends PeriodicSlamStep implements Runnable {
  private final Thread thread = new Thread(this);
  private final SlamWaypointDetection slamWaypointDetection;
  private double visibleBoxXMin;
  private double visibleBoxXMax;
  private double visibleBoxHalfWidth;
  // ---
  private MapProvider occurrenceMap;
  private boolean isLaunched;

  public SlamMapProcessing(SlamContainer slamContainer, SlamConfig slamConfig) {
    super(slamContainer, slamConfig.waypointUpdateRate);
    slamWaypointDetection = new SlamWaypointDetection(slamConfig);
    visibleBoxXMin = Magnitude.METER.toDouble(slamConfig.visibleBoxXMin);
    visibleBoxXMax = Magnitude.METER.toDouble(slamConfig.visibleBoxXMax);
    visibleBoxHalfWidth = Magnitude.METER.toDouble(slamConfig.visibleBoxHalfWidth);
    isLaunched = true;
    thread.start();
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    occurrenceMap = slamContainer.getOccurrenceMap();
    thread.interrupt();
  }

  @Override // from Runnable
  public void run() {
    while (isLaunched)
      if (Objects.nonNull(occurrenceMap)) {
        mapProcessing();
        occurrenceMap = null;
      } else
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          // ---
        }
  }

  private void mapProcessing() {
    List<double[]> worldWaypoints = slamWaypointDetection.detectWaypoints(occurrenceMap);
    slamContainer.setWaypoints(SlamMapProcessingUtil.getWaypoints( //
        worldWaypoints, slamContainer.getPoseUnitless(), //
        visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth));
  }
}
