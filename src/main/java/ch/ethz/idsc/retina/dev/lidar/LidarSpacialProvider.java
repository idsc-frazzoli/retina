// code by jph
package ch.ethz.idsc.retina.dev.lidar;

/**
 * 
 */
public interface LidarSpacialProvider extends LidarRayDataListener {
  void addListener(LidarSpacialEventListener lidarSpacialEventListener);
}
