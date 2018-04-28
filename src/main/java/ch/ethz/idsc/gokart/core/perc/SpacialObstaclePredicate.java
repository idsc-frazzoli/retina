// code by jph
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Tensor;

public interface SpacialObstaclePredicate {
  /** @param point in lidar coordinates (px, py, pz)
   * @return true if given point represents an obstacle for the gokart */
  boolean isObstacle(Tensor point);
}
