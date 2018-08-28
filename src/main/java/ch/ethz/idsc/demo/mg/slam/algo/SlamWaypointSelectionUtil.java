// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.demo.mg.slam.SlamWaypointUtil;

/* package */ enum SlamWaypointSelectionUtil {
  ;
  // TODO MG idea for v1.0: selected way point that is farthest away
  public static void selectWaypoint(SlamContainer slamContainer) {
    List<SlamWaypoint> slamWayPoints = slamContainer.getSlamWaypoints();
    int numberOfWaypoints = slamWayPoints.size();
    if (numberOfWaypoints == 0)
      return;
    double[] waypointDistancesX = new double[numberOfWaypoints];
    int maxIndex = 0;
    for (int i = 0; i < numberOfWaypoints; i++) {
      waypointDistancesX[i] = SlamWaypointUtil.computeGokartPosition(slamWayPoints.get(i).getWorldPosition(), //
          slamContainer.getPoseUnitless())[0];
      maxIndex = waypointDistancesX[i] > waypointDistancesX[maxIndex] ? i : maxIndex;
    }
    slamContainer.setSelectedSlamWaypoint(slamWayPoints.get(maxIndex));
  }
}
