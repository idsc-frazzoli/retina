// code by jph
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Tensor;

public interface SpacialObstaclePredicate {
  /** @param point in lidar coordinates (px, py, pz)
   * @return true if given point represents an obstacle for the gokart */
  boolean isObstacle(Tensor point);

  /** @param x coordinate in lidar frame, i.e. in the front of the gokart
   * @param z coordinate in lidar frame, i.e. up-down distance from lidar height
   * @return true if given point represents an obstacle for the gokart */
  boolean isObstacle(double x, double z);
}
