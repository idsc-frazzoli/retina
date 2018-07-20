// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.WayPoint;
import ch.ethz.idsc.demo.mg.util.slam.SlamMapProcessingUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;

// module receives a set of waypoints in world frame and outputs a trajectory
public class SlamTrajectoryPlanning {
  private final GokartPoseInterface gokartPose;
  private final double initialDelay;
  private final double trajectoryUpdateRate;
  private final double visibleBoxXMin;
  private final double visibleBoxXMax;
  private final double visibleBoxHalfWidth;
  private List<WayPoint> gokartWayPoints;
  private List<WayPoint> visibleGokartWayPoints;
  private double lastComputationTimeStamp;
  private int purePursuitIndex;

  SlamTrajectoryPlanning(SlamConfig slamConfig, GokartPoseInterface gokartPose) {
    this.gokartPose = gokartPose;
    initialDelay = slamConfig.initialDelay.number().doubleValue();
    trajectoryUpdateRate = slamConfig.trajectoryUpdateRate.number().doubleValue();
    visibleBoxXMin = slamConfig.visibleBoxXMin.number().doubleValue();
    visibleBoxXMax = slamConfig.visibleBoxXMax.number().doubleValue();
    visibleBoxHalfWidth = slamConfig.visibleBoxHalfWidth.number().doubleValue();
  }

  public void initialize(double initTimeStamp) {
    gokartWayPoints = new ArrayList<WayPoint>();
    visibleGokartWayPoints = new ArrayList<WayPoint>();
    lastComputationTimeStamp = initTimeStamp + initialDelay;
  }

  public void computeTrajectory(List<double[]> worldWayPoints, double currentTimeStamp) {
    if (currentTimeStamp - lastComputationTimeStamp > trajectoryUpdateRate) {
      gokartWayPoints = new ArrayList<WayPoint>(worldWayPoints.size());
      visibleGokartWayPoints = new ArrayList<WayPoint>();
      SlamMapProcessingUtil.setGokartWayPoints(worldWayPoints, gokartWayPoints, gokartPose.getPose());
      SlamMapProcessingUtil.checkVisibility(gokartWayPoints, visibleGokartWayPoints, visibleBoxXMin, visibleBoxXMax, visibleBoxHalfWidth);
      SlamMapProcessingUtil.choosePurePursuitPoint(visibleGokartWayPoints, purePursuitIndex);
      lastComputationTimeStamp = currentTimeStamp;
    }
  }

  public List<WayPoint> getWayPoints() {
    return gokartWayPoints;
  }

  public double[] getPurePursuitPoint() {
    if (purePursuitIndex != -1) {
      return visibleGokartWayPoints.get(purePursuitIndex).getGokartPosition();
    } else {
      System.out.println("FATAL: no visible waypoint");
      double[] straightWayPoint = { 10, 0 };
      return straightWayPoint;
    }
  }
}
