// code by jph
package ch.ethz.idsc.gokart.core.perc;

/** interfaces uses float precision because typical sensors do not justify higher accuracy */
public interface SpacialXZObstaclePredicate extends SpacialObstaclePredicate {
  /** @param x coordinate in lidar frame, i.e. in the front
   * @param z coordinate in lidar frame, i.e. up-down distance from lidar height
   * @return true if given point represents an obstacle */
  boolean isObstacle(float x, float z);
}
