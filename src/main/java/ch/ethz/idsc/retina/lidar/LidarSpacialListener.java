// code by jph
package ch.ethz.idsc.retina.lidar;

@FunctionalInterface
public interface LidarSpacialListener {
  void lidarSpacial(LidarXYZEvent lidarXYZEvent);
}
