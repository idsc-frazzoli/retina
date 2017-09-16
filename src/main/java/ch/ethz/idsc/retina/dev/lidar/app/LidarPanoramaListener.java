// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

public interface LidarPanoramaListener extends AutoCloseable {
  void lidarPanorama(LidarPanorama lidarPanorama);
}
