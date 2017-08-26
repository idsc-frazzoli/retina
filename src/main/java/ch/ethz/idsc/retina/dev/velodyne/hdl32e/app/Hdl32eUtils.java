// code by jph
package ch.ethz.idsc.retina.dev.velodyne.hdl32e.app;

import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32ePosDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32eRayDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaFrame;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32eSpacialProvider;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.LidarRotationProvider;

public enum Hdl32eUtils {
  ;
  public static Hdl32ePanoramaFrame createPanoramaDisplay(Hdl32eRayDecoder hdl32eRayDecoder) {
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    hdl32eRayDecoder.addListener(hdl32ePanoramaCollector);
    return hdl32ePanoramaFrame;
  }

  public static VelodyneRayFrame createRayFrame(Hdl32eRayDecoder hdl32eRayDecoder, Hdl32ePosDecoder hdl32ePosDecoder) {
    LidarAngularFiringCollector lidarAngularFiringCollector = createCollector(hdl32eRayDecoder, hdl32ePosDecoder);
    VelodyneRayFrame velodyneFiringFrame = new VelodyneRayFrame();
    lidarAngularFiringCollector.addListener(velodyneFiringFrame);
    hdl32ePosDecoder.addListener(velodyneFiringFrame);
    return velodyneFiringFrame;
  }

  public static LidarAngularFiringCollector createCollector(Hdl32eRayDecoder hdl32eRayDecoder, Hdl32ePosDecoder hdl32ePosDecoder) {
    LidarAngularFiringCollector lidarAngularFiringCollector = LidarAngularFiringCollector.createDefault();
    Hdl32eSpacialProvider hdl32eSpacialProvider = new Hdl32eSpacialProvider();
    hdl32eSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider hdl32eRotationProvider = new LidarRotationProvider();
    hdl32eRotationProvider.addListener(lidarAngularFiringCollector);
    hdl32eRayDecoder.addListener(hdl32eSpacialProvider);
    hdl32eRayDecoder.addListener(hdl32eRotationProvider);
    return lidarAngularFiringCollector;
  }
}
