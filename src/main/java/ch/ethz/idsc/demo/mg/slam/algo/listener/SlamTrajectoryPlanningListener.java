// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.listener;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.algo.SlamTrajectoryPlanningUtil;
import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** receives a set of way points in world frame and outputs a trajectory */
/* package */ class SlamTrajectoryPlanningListener implements DavisDvsListener, Runnable {
  private final SlamMapProcessingListener slamMapProcessingListener;
  private final SlamContainer slamContainer;
  private final double trajectoryUpdateRate;
  private final double visibleBoxXMin;
  private final double visibleBoxXMax;
  private final double visibleBoxHalfWidth;
  private final Thread thread = new Thread(this);
  // ---
  private List<double[]> worldWayPoints;
  private double lastComputationTimeStamp;
  private boolean isLaunched;

  public SlamTrajectoryPlanningListener(SlamConfig slamConfig, SlamContainer slamContainer, SlamMapProcessingListener slamMapProcessingListener) {
    this.slamMapProcessingListener = slamMapProcessingListener;
    this.slamContainer = slamContainer;
    trajectoryUpdateRate = Magnitude.SECOND.toDouble(slamConfig.trajectoryUpdateRate);
    visibleBoxXMin = Magnitude.METER.toDouble(slamConfig.visibleBoxXMin);
    visibleBoxXMax = Magnitude.METER.toDouble(slamConfig.visibleBoxXMax);
    visibleBoxHalfWidth = (visibleBoxXMax - visibleBoxXMin) * 0.5;
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    if (!isLaunched) {
      isLaunched = true;
      thread.start();
    }
    double currentTimeStamp = davisDvsEvent.time * 1E-6;
    if (currentTimeStamp - lastComputationTimeStamp > trajectoryUpdateRate) {
      worldWayPoints = slamMapProcessingListener.getWorldWayPoints();
      thread.interrupt();
      lastComputationTimeStamp = currentTimeStamp;
    }
  }

  @Override // from Runnable
  public void run() {
    while (isLaunched)
      if (Objects.nonNull(worldWayPoints)) {
        trajectoryPlanning();
        worldWayPoints = null;
      } else
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          // ---
        }
  }

  private void trajectoryPlanning() {
    slamContainer.setWayPoints(SlamTrajectoryPlanningUtil.getGokartWayPoints(worldWayPoints, slamContainer.getSlamEstimatedPose().getPoseUnitless()));
    SlamTrajectoryPlanningUtil.checkVisibility(slamContainer.getWayPoints(), visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
  }
}
