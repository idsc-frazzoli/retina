// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.WayPoint;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** module receives a set of waypoints in world frame and outputs a trajectory */
class SlamTrajectoryPlanning implements Runnable {
  private final SlamEstimatedPose estimatedPose;
  private final double initialDelay;
  private final double trajectoryUpdateRate;
  private final double visibleBoxXMin;
  private final double visibleBoxXMax;
  private final double visibleBoxHalfWidth;
  private final Thread thread = new Thread(this);
  // ---
  private List<double[]> worldWayPoints;
  private List<WayPoint> gokartWayPoints;
  private double lastComputationTimeStamp;
  private boolean isLaunched;

  SlamTrajectoryPlanning(SlamConfig slamConfig, SlamEstimatedPose estimatedPose) {
    this.estimatedPose = estimatedPose;
    initialDelay = Magnitude.SECOND.toDouble(slamConfig._initialDelay);
    trajectoryUpdateRate = Magnitude.SECOND.toDouble(slamConfig._trajectoryUpdateRate);
    visibleBoxXMin = slamConfig.visibleBoxXMin.number().doubleValue();
    visibleBoxXMax = slamConfig.visibleBoxXMax.number().doubleValue();
    visibleBoxHalfWidth = slamConfig.visibleBoxHalfWidth.number().doubleValue();
  }

  public void initialize(double initTimeStamp) {
    gokartWayPoints = new ArrayList<>();
    lastComputationTimeStamp = initTimeStamp + initialDelay;
    thread.start();
  }

  public void stop() {
    isLaunched = false;
    thread.interrupt();
  }

  // TODO JPH use timertask
  public void computeTrajectory(List<double[]> worldWayPoints, double currentTimeStamp) {
    if (currentTimeStamp - lastComputationTimeStamp > trajectoryUpdateRate) {
      this.worldWayPoints = worldWayPoints;
      thread.interrupt();
      lastComputationTimeStamp = currentTimeStamp;
    }
  }

  /** for visualization purposes
   * 
   * @return gokartWayPoints all detected waypoints */
  public List<WayPoint> getWayPoints() {
    return gokartWayPoints;
  }

  @Override
  public void run() {
    while (isLaunched)
      if (Objects.nonNull(worldWayPoints)) {
        gokartWayPoints = SlamTrajectoryPlanningUtil.getGokartWayPoints(worldWayPoints, estimatedPose.getPoseUnitless());
        SlamTrajectoryPlanningUtil.checkVisibility(gokartWayPoints, visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
        worldWayPoints = null;
      } else
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          // ---
        }
  }
}
