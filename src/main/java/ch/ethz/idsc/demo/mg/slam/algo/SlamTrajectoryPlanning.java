// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamEstimatedPose;
import ch.ethz.idsc.demo.mg.slam.WayPoint;
import ch.ethz.idsc.demo.mg.util.slam.SlamMapProcessingUtil;
import ch.ethz.idsc.owl.data.Stopwatch;

// module receives a set of waypoints in world frame and outputs a trajectory
public class SlamTrajectoryPlanning {
  private final SlamEstimatedPose estimatedPose;
  private final double initialDelay;
  private final double trajectoryUpdateRate;
  private final double visibleBoxXMin;
  private final double visibleBoxXMax;
  private final double visibleBoxHalfWidth;
  private List<WayPoint> gokartWayPoints;
  private List<WayPoint> visibleGokartWayPoints;
  private double lastComputationTimeStamp;
  private int purePursuitIndex;

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
    visibleGokartWayPoints = new ArrayList<>();
    lastComputationTimeStamp = initTimeStamp + initialDelay;
  }

  public void computeTrajectory(List<double[]> worldWayPoints, double currentTimeStamp) {
    if (currentTimeStamp - lastComputationTimeStamp > trajectoryUpdateRate) {
      Stopwatch stopWatch = Stopwatch.started();
      gokartWayPoints = new ArrayList<>(worldWayPoints.size());
      visibleGokartWayPoints = new ArrayList<>();
      SlamMapProcessingUtil.setGokartWayPoints(worldWayPoints, gokartWayPoints, estimatedPose.getPoseUnitless());
      SlamMapProcessingUtil.checkVisibility(gokartWayPoints, visibleGokartWayPoints, visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
      SlamMapProcessingUtil.choosePurePursuitPoint(visibleGokartWayPoints, purePursuitIndex);
//      System.out.println(stopWatch.display_seconds());
      lastComputationTimeStamp = currentTimeStamp;
    }
  }

  /** for visualization purposes
   * 
   * @return gokartWayPoints all detected waypoints */
  public List<WayPoint> getWayPoints() {
    return gokartWayPoints;
  }

  public double[] getPurePursuitPoint() {
    if (purePursuitIndex != -1) {
      return visibleGokartWayPoints.get(purePursuitIndex).getGokartPosition();
    }
    System.out.println("FATAL: no visible waypoint");
    double[] straightWayPoint = { 10, 0 };
    return straightWayPoint;
  }
}
