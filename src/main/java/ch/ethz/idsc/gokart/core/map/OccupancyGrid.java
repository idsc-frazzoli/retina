// code by mh
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

// TODO JPH/MH if a plannable occumancy grid is needed it can extend from this interface occupancy grid
/* package */ interface OccupancyGrid extends Region<Tensor> {
  /** @return vector of length 2 */
  Tensor getGridSize();

  /** @param x
   * @param y
   * @return if cell at grid coordinate (x, y) is occupied */
  boolean isCellOccupied(int x, int y);

  /** @return world coordinates to grid coordinates */
  Tensor getTransform();
}
