// code by jph
package ch.ethz.idsc.retina.dev.velodyne.hdl32e;

import ch.ethz.idsc.retina.dev.velodyne.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.velodyne.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.velodyne.VelodynePosDecoder;
import ch.ethz.idsc.retina.dev.velodyne.VelodyneRayDecoder;
import ch.ethz.idsc.retina.dev.velodyne.app.VelodyneRayFrame;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaFrame;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32eSpacialProvider;

public enum Hdl32eUtils {
  ;
  public static Hdl32ePanoramaFrame createPanoramaDisplay(Hdl32eRayDecoder hdl32eRayDecoder) {
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    hdl32eRayDecoder.addListener(hdl32ePanoramaCollector);
    return hdl32ePanoramaFrame;
  }

  public static VelodyneRayFrame createRayFrame(VelodyneRayDecoder hdl32eRayDecoder, VelodynePosDecoder hdl32ePosDecoder) {
    LidarAngularFiringCollector lidarAngularFiringCollector = createCollector(hdl32eRayDecoder, hdl32ePosDecoder);
    VelodyneRayFrame velodyneFiringFrame = new VelodyneRayFrame();
    lidarAngularFiringCollector.addListener(velodyneFiringFrame);
    Hdl32ePosDecoder pdec = (Hdl32ePosDecoder) hdl32ePosDecoder;
    pdec.addListener(velodyneFiringFrame);
    return velodyneFiringFrame;
  }

  public static LidarAngularFiringCollector createCollector(VelodyneRayDecoder hdl32eRayDecoder, VelodynePosDecoder hdl32ePosDecoder) {
    LidarAngularFiringCollector lidarAngularFiringCollector = LidarAngularFiringCollector.createDefault();
    Hdl32eSpacialProvider hdl32eSpacialProvider = new Hdl32eSpacialProvider();
    hdl32eSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider hdl32eRotationProvider = new LidarRotationProvider();
    hdl32eRotationProvider.addListener(lidarAngularFiringCollector);
    Hdl32eRayDecoder rdec = (Hdl32eRayDecoder) hdl32eRayDecoder;
    rdec.addListener(hdl32eSpacialProvider);
    rdec.addListener(hdl32eRotationProvider);
    return lidarAngularFiringCollector;
  }
}
