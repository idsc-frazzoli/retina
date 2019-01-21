// code by jph
package ch.ethz.idsc.retina.lidar;

@FunctionalInterface
public interface LidarRotationListener {
  void lidarRotation(LidarRotationEvent lidarRotationEvent);
}
