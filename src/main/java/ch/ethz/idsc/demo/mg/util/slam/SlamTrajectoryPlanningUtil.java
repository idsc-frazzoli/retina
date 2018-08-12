// code by mg
package ch.ethz.idsc.demo.mg.util.slam;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.demo.mg.slam.WayPoint;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.Inverse;

// static methods to facilitate map and waypoint processing
public enum SlamTrajectoryPlanningUtil {
  ;
  /** get waypoint objects according to world frame waypoint positions
   * 
   * @param worldWayPoints [m] in world frame
   * @param currentPose unitless representation
   * @return gokartWayPoints */
  public static List<WayPoint> getGokartWayPoints(List<double[]> worldWayPoints, Tensor currentPose) {
    GeometricLayer worldToGokartLayer = GeometricLayer.of(Inverse.of(Se2Utils.toSE2Matrix(currentPose)));
    List<WayPoint> gokartWayPoints = new ArrayList<>(worldWayPoints.size());
    for (int i = 0; i < worldWayPoints.size(); i++) {
      double[] worldPosition = worldWayPoints.get(i);
      WayPoint slamWayPoint = new WayPoint(worldPosition);
      Tensor gokartPosition = worldToGokartLayer.toVector(worldWayPoints.get(i)[0], worldWayPoints.get(i)[1]);
      slamWayPoint.setGokartPosition(gokartPosition);
      gokartWayPoints.add(i, slamWayPoint);
    }
    return gokartWayPoints;
  }

  /** sets visibility field of waypoints
   * 
   * @param gokartWayPoints
   * @param visibleBoxXMin [m] in go kart frame
   * @param visibleBoxXMax [m] in go kart frame
   * @param visibleBoxHalfWidth [m] in go kart frame */
  public static void checkVisibility(List<WayPoint> gokartWayPoints, double visibleBoxXMin, double visibleBoxXMax, double visibleBoxHalfWidth) {
    for (int i = 0; i < gokartWayPoints.size(); i++) {
      double[] gokartPosition = gokartWayPoints.get(i).getGokartPosition();
      if (gokartPosition[0] > visibleBoxXMin && gokartPosition[1] < visibleBoxXMax && gokartPosition[1] > -visibleBoxHalfWidth
          && gokartPosition[1] < visibleBoxHalfWidth) {
        gokartWayPoints.get(i).setVisibility(true);
      }
    }
  }

  /** finds visible waypoint that is furthest away
   * 
   * @param visibleGokartWayPoints
   * @param purePursuitIndex index of element in visibleGokartWayPoints that is furthest away */
  public static void choosePurePursuitPoint(List<WayPoint> visibleGokartWayPoints, int purePursuitIndex) {
    double maxDistance = 0;
    purePursuitIndex = -1;
    for (int i = 0; i < visibleGokartWayPoints.size(); i++) {
      if (visibleGokartWayPoints.get(i).getGokartPosition()[0] > maxDistance) {
        maxDistance = visibleGokartWayPoints.get(i).getGokartPosition()[0];
        purePursuitIndex = i;
      }
    }
  }
  // idea: attention module that guesses position of next waypoint
}
