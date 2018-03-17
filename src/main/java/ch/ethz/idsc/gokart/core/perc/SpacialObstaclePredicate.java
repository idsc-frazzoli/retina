package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Tensor;

public interface SpacialObstaclePredicate {
  /** @param x point in lidar coordinates
   * @return true if given point does not belong to floor and */
  boolean isObstacle(Tensor x);
}
