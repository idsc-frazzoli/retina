// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;

// module receives a set of waypoints in world frame and outputs a trajectory
public class SlamTrajectoryPlanning {
  private final GokartPoseInterface gokartPose;
  private List<WayPoint> gokartWayPoints;
  private List<WayPoint> visibleGokartWayPoints;
  private double lastComputationTimeStamp;
  private int purePursuitIndex;

  SlamTrajectoryPlanning(SlamConfig slamConfig, GokartPoseInterface gokartPose) {
    this.gokartPose = gokartPose;
  }

  public void initialize(double initTimeStamp) {
    gokartWayPoints = new ArrayList<WayPoint>();
    visibleGokartWayPoints = new ArrayList<WayPoint>();
    lastComputationTimeStamp = initTimeStamp + 1; // TODO magic constant
  }

  public void computeTrajectory(List<double[]> worldWayPoints, double currentTimeStamp) {
    if (currentTimeStamp - lastComputationTimeStamp > 0.1) {
      gokartWayPoints = new ArrayList<WayPoint>(worldWayPoints.size());
      visibleGokartWayPoints = new ArrayList<WayPoint>();
      setGokartWayPoints(worldWayPoints);
      checkVisibility();
      choosePurePursuitPoint();
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
      return null;
    }
  }

  // transforms the waypoints from world frame to go kart frame
  private void setGokartWayPoints(List<double[]> worldWayPoints) {
    Tensor currentPose = gokartPose.getPose();
    GeometricLayer worldToGokartLayer = GeometricLayer.of(Inverse.of(GokartPoseHelper.toSE2Matrix(currentPose)));
    for (int i = 0; i < worldWayPoints.size(); i++) {
      double[] worldPosition = worldWayPoints.get(i);
      WayPoint slamWayPoint = new WayPoint(worldPosition);
      Tensor gokartPosition = worldToGokartLayer.toVector(worldWayPoints.get(i)[0], worldWayPoints.get(i)[1]);
      slamWayPoint.setGokartPosition(gokartPosition);
      gokartWayPoints.add(i, slamWayPoint);
    }
  }

  // checks which waypoints are currently visible TODO could be done with lookup table
  private void checkVisibility() {
    for (int i = 0; i < gokartWayPoints.size(); i++) {
      double[] gokartPosition = gokartWayPoints.get(i).getGokartPosition();
      if (gokartPosition[0] > 1 && gokartPosition[1] < 10 && gokartPosition[1] > -5 && gokartPosition[1] < 5) {
        gokartWayPoints.get(i).setVisibility(true);
        visibleGokartWayPoints.add(gokartWayPoints.get(i));
      }
    }
  }

  // chooses way point for pure pursuit
  private void choosePurePursuitPoint() {
    double maxDistance = 0;
    purePursuitIndex = -1;
    for (int i = 0; i < visibleGokartWayPoints.size(); i++) {
      if (visibleGokartWayPoints.get(i).getGokartPosition()[0] > maxDistance) {
        maxDistance = visibleGokartWayPoints.get(i).getGokartPosition()[0];
        purePursuitIndex = i;
      }
    }
  }
}
