// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.WayPoint;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;

/** static methods to facilitate map and waypoint processing */
enum SlamTrajectoryPlanningUtil {
  ;
  /** get waypoint objects according to world frame waypoint positions
   * 
   * @param worldWayPoints [m] in world frame
   * @param currentPose unitless representation
   * @return gokartWayPoints */
  public static List<WayPoint> getGokartWayPoints(List<double[]> worldWayPoints, Tensor currentPose) {
    GeometricLayer worldToGokartLayer = GeometricLayer.of(Inverse.of(Se2Utils.toSE2Matrix(currentPose)));
    List<WayPoint> gokartWayPoints = new ArrayList<>(worldWayPoints.size());
    for (int index = 0; index < worldWayPoints.size(); ++index) {
      double[] worldPosition = worldWayPoints.get(index);
      WayPoint slamWayPoint = new WayPoint(worldPosition);
      Tensor gokartPosition = worldToGokartLayer.toVector( //
          worldPosition[0], //
          worldPosition[1]);
      slamWayPoint.setGokartPosition(gokartPosition);
      gokartWayPoints.add(index, slamWayPoint);
    }
    return gokartWayPoints;
  }

  /** sets visibility field of way points
   * 
   * @param gokartWayPoints
   * @param visibleBoxXMin [m] in go kart frame
   * @param visibleBoxXMax [m] in go kart frame
   * @param visibleBoxHalfWidth [m] in go kart frame */
  public static void checkVisibility(List<WayPoint> gokartWayPoints, double visibleBoxXMin, double visibleBoxXMax, double visibleBoxHalfWidth) {
    for (WayPoint wayPoint : gokartWayPoints) {
      double[] gokartPosition = wayPoint.getGokartPosition();
      if (gokartPosition[0] > visibleBoxXMin && gokartPosition[0] < visibleBoxXMax && //
          gokartPosition[1] > -visibleBoxHalfWidth && gokartPosition[1] < visibleBoxHalfWidth)
        wayPoint.setVisibility(true);
      else
        wayPoint.setVisibility(false);
    }
  }

  /** finds visible way point that is farthest away
   * 
   * @param visibleGokartWayPoints */
  // TODO function not used
  public static int choosePurePursuitPoint(List<WayPoint> visibleGokartWayPoints) {
    // TODO filter criteria also should consider corridor of steering:
    // ... a close point on the center line is better than a further point to the side that can't be reached
    double maxDistance = 0;
    int purePursuitIndex = -1;
    for (int index = 0; index < visibleGokartWayPoints.size(); ++index) {
      WayPoint wayPoint = visibleGokartWayPoints.get(index);
      if (maxDistance < wayPoint.getGokartPosition()[0]) {
        maxDistance = wayPoint.getGokartPosition()[0];
        purePursuitIndex = index;
      }
    }
    return purePursuitIndex;
  }
  // idea: attention module that guesses position of next way point
}
