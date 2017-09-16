// code by jph
package ch.ethz.idsc.retina.dev.lidar;

public interface LidarRayBlockListener {
  /** @param floatBuffer
   * @param byteBuffer */
  // TODO probably should provide time info
  void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent);
}
