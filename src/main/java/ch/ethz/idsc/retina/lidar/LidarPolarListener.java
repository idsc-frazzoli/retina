// code by jph, gjoel
package ch.ethz.idsc.retina.lidar;

@FunctionalInterface
public interface LidarPolarListener {
  void lidarPolar(LidarPolarEvent lidarPolarEvent);
}
