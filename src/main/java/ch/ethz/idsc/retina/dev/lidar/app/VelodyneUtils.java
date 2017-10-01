// code by jph
package ch.ethz.idsc.retina.dev.lidar.app;

import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRayDataProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32eSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16SpacialProvider;

public enum VelodyneUtils {
  ;
  public static void panorama(LidarRayDataProvider lidarRayDataProvider, LidarPanoramaProvider lidarPanoramaProvider) {
    LidarPanoramaFrame lidarPanoramaFrame = new LidarPanoramaFrame();
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarPanoramaProvider);
    lidarPanoramaProvider.addListener(lidarPanoramaFrame);
    lidarRayDataProvider.addRayListener(lidarRotationProvider);
    lidarRayDataProvider.addRayListener(lidarPanoramaProvider);
  }

  public static LidarAngularFiringCollector createCollector32(VelodyneDecoder velodyneDecoder) {
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304 * 32, 3);
    LidarSpacialProvider lidarSpacialProvider = new Hdl32eSpacialProvider();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    return lidarAngularFiringCollector;
  }

  public static LidarAngularFiringCollector createCollector16(VelodyneDecoder velodyneDecoder) {
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(2304 * 32, 3);
    LidarSpacialProvider lidarSpacialProvider = new Vlp16SpacialProvider();
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    return lidarAngularFiringCollector;
  }
}
