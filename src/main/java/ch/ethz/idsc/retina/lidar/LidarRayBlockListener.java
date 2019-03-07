// code by jph
package ch.ethz.idsc.retina.lidar;

@FunctionalInterface
public interface LidarRayBlockListener {
  /** @param lidarRayBlockEvent */
  void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent);
}
