// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.List;

/** listener receives world way points */
public interface WorldWaypointListener {
  /** @param worldWaypoints in world frame coordinates */
  void worldWaypoints(List<double[]> worldWaypoints);
}
