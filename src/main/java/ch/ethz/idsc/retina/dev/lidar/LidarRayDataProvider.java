// code by jph
package ch.ethz.idsc.retina.dev.lidar;

public interface LidarRayDataProvider {
  /** @param lidarRayDataListener */
  void addRayListener(LidarRayDataListener lidarRayDataListener);

  /** @return */
  boolean hasRayListeners();
}
