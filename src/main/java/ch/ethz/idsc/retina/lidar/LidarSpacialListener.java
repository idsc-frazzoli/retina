// code by jph
package ch.ethz.idsc.retina.lidar;

@FunctionalInterface
public interface LidarSpacialListener {
  void lidarSpacial(LidarSpacialEvent lidarSpacialEvent);
}
