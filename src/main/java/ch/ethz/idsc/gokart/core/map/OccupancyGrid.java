// code by mh
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;

// TODO JPH if a planable occupancy grid is needed it can extend from this interface occupancy grid
public interface OccupancyGrid extends Region<Tensor> {
  /** @return vector of length 2 */
  Tensor getGridSize();

  /** @param x
   * @param y
   * @return if cell at grid coordinate (x, y) is occupied */
  boolean isCellOccupied(int x, int y);

  /** @return world coordinates to grid coordinates */
  Tensor getTransform();

  /** clear area around starting position */
  // TODO JPH function obsolete, also bad API
  void clearStart(int startX, int startY, double orientation);
}
