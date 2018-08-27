// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.List;

import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.SlamWaypoint;
import ch.ethz.idsc.demo.mg.slam.SlamWaypointUtil;

/* package */ enum SlamWaypointSelectionUtil {
  ;
  // TODO MG idea for v1.0: selected way point that is furthest away
  public static void selectWaypoint(SlamContainer slamContainer) {
    List<SlamWaypoint> slamWayPoints = slamContainer.getWaypoints();
    int numberOfWaypoints = slamWayPoints.size();
    double[] waypointDistances = new double[numberOfWaypoints];
    for (int i = 0; i < numberOfWaypoints; i++) {
      waypointDistances[i] = SlamWaypointUtil.computeGokartPosition(slamWayPoints.get(i).getWorldPosition(), //
          slamContainer.getPoseUnitless())[0];
    }
  }
}
