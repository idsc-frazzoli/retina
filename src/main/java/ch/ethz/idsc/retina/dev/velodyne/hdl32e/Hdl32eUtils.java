// code by jph
package ch.ethz.idsc.retina.dev.velodyne.hdl32e;

import ch.ethz.idsc.retina.dev.velodyne.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.velodyne.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.velodyne.app.VelodyneRayFrame;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaFrame;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32eSpacialProvider;

public enum Hdl32eUtils {
  ;
  public static Hdl32ePanoramaFrame createPanoramaDisplay(Hdl32eDecoder hdl32eRayDecoder) {
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    hdl32eRayDecoder.addRayListener(hdl32ePanoramaCollector);
    return hdl32ePanoramaFrame;
  }

  public static VelodyneRayFrame createRayFrame(Hdl32eDecoder hdl32eDecoder) {
    LidarAngularFiringCollector lidarAngularFiringCollector = createCollector(hdl32eDecoder);
    VelodyneRayFrame velodyneFiringFrame = new VelodyneRayFrame();
    lidarAngularFiringCollector.addListener(velodyneFiringFrame);
    hdl32eDecoder.addPosListener(velodyneFiringFrame);
    return velodyneFiringFrame;
  }

  public static LidarAngularFiringCollector createCollector(Hdl32eDecoder hdl32eDecoder) {
    LidarAngularFiringCollector lidarAngularFiringCollector = LidarAngularFiringCollector.createDefault();
    Hdl32eSpacialProvider hdl32eSpacialProvider = new Hdl32eSpacialProvider();
    hdl32eSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider hdl32eRotationProvider = new LidarRotationProvider();
    hdl32eRotationProvider.addListener(lidarAngularFiringCollector);
    hdl32eDecoder.addRayListener(hdl32eSpacialProvider);
    hdl32eDecoder.addRayListener(hdl32eRotationProvider);
    return lidarAngularFiringCollector;
  }
}
