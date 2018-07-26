// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamEstimatedPose;
import ch.ethz.idsc.demo.mg.slam.WayPoint;
import ch.ethz.idsc.demo.mg.util.slam.SlamMapProcessingUtil;

// module receives a set of waypoints in world frame and outputs a trajectory
public class SlamTrajectoryPlanning implements Runnable {
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

  SlamTrajectoryPlanning(SlamConfig slamConfig, SlamEstimatedPose estimatedPose) {
    this.estimatedPose = estimatedPose;
    initialDelay = slamConfig.initialDelay.number().doubleValue();
    trajectoryUpdateRate = slamConfig.trajectoryUpdateRate.number().doubleValue();
    visibleBoxXMin = slamConfig.visibleBoxXMin.number().doubleValue();
    visibleBoxXMax = slamConfig.visibleBoxXMax.number().doubleValue();
    visibleBoxHalfWidth = slamConfig.visibleBoxHalfWidth.number().doubleValue();
  }

  public void initialize(double initTimeStamp) {
    gokartWayPoints = new ArrayList<>();
    lastComputationTimeStamp = initTimeStamp + initialDelay;
    thread.start();
  }

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
    while (true) {
      if (Objects.nonNull(worldWayPoints)) {
        gokartWayPoints = SlamMapProcessingUtil.getGokartWayPoints(worldWayPoints, estimatedPose.getPoseUnitless());
        SlamMapProcessingUtil.checkVisibility(gokartWayPoints, visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
        worldWayPoints = null;
      } else {
        try {
          Thread.sleep(0);
        } catch (InterruptedException e) {
          // ---
        }
      }
    }
  }
}
