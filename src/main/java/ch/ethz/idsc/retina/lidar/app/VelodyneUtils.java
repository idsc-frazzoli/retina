// code by jph
package ch.ethz.idsc.retina.lidar.app;

import ch.ethz.idsc.retina.lidar.LidarRayDataProvider;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;

public enum VelodyneUtils {
  ;
  public static LidarPanoramaFrame panorama(LidarRayDataProvider lidarRayDataProvider, LidarPanoramaProvider lidarPanoramaProvider) {
    LidarPanoramaFrame lidarPanoramaFrame = new LidarPanoramaFrame();
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarPanoramaProvider);
    lidarPanoramaProvider.addListener(lidarPanoramaFrame);
    lidarRayDataProvider.addRayListener(lidarRotationProvider);
    lidarRayDataProvider.addRayListener(lidarPanoramaProvider);
    return lidarPanoramaFrame;
  }
}
