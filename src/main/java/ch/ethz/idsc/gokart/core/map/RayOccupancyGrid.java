// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.tensor.Tensor;

/* package */ interface RayOccupancyGrid extends OccupancyGrid, RenderInterface {

  /** process a new lidar observation and update the occupancy map
   *
   * @param pos 2D position of new lidar observation in gokart coordinates
   * @param type of observation either 0, or 1 */
  void processObservation(Tensor pos, int type);

  /** set vehicle pose w.r.t world frame
   *
   * @param pose vector of the form {px, py, heading} */
  void setPose(Tensor pose);

  /** clears current obstacle image and redraws all known obstacles */
  void genObstacleMap();
}
