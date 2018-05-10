// code by jph
package ch.ethz.idsc.gokart.core.perc;

public interface SpacialXZObstaclePredicate extends SpacialObstaclePredicate {
  /** @param x coordinate in lidar frame, i.e. in the front
   * @param z coordinate in lidar frame, i.e. up-down distance from lidar height
   * @return true if given point represents an obstacle */
  boolean isObstacle(double x, double z);
}
