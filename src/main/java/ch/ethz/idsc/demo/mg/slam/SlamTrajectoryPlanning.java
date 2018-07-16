// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;

// module receives a set of waypoints and then computes a trajectry based on that
public class SlamTrajectoryPlanning {
  private final List<double[]> worldWayPoints;

  SlamTrajectoryPlanning(SlamConfig slamConfig, GokartPoseInterface gokartPoseInterface) {
    worldWayPoints = new ArrayList<>();
  }

  public void setWorldWayPoints(List<double[]> worldWayPoints) {
  }

  // generate trajectory by connecting way points
  private void createTrajectory() {
    // ..
  }
}
