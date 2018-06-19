// code by jph
package ch.ethz.idsc.gokart.core.perc;

import ch.ethz.idsc.tensor.Tensor;

/** implementations specify the reference frame of the input point to be
 * in lidar coordinates, global coordinates, or other */
public interface SpacialObstaclePredicate {
  /** @param point of the form {px, py, pz}
   * @return true if given point represents an obstacle */
  boolean isObstacle(Tensor point);
}
