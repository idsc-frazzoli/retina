// code by jph
package ch.ethz.idsc.retina.dev.velodyne.vlp16;

import ch.ethz.idsc.retina.dev.velodyne.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.velodyne.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.velodyne.app.VelodyneRayFrame;

public enum Vlp16Utils {
  ;
  // public static Hdl32ePanoramaFrame createPanoramaDisplay(Hdl32eRayDecoder hdl32eRayDecoder) {
  // Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
  // Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
  // hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
  // hdl32eRayDecoder.addListener(hdl32ePanoramaCollector);
  // return hdl32ePanoramaFrame;
  // }
  //
  public static VelodyneRayFrame createRayFrame(Vlp16RayDecoder vlp16RayDecoder, Vlp16PosDecoder vlp16PosDecoder) {
    LidarAngularFiringCollector lidarAngularFiringCollector = createCollector(vlp16RayDecoder, vlp16PosDecoder);
    VelodyneRayFrame velodyneFiringFrame = new VelodyneRayFrame();
    lidarAngularFiringCollector.addListener(velodyneFiringFrame);
    vlp16PosDecoder.addListener(velodyneFiringFrame);
    return velodyneFiringFrame;
  }

  public static LidarAngularFiringCollector createCollector(Vlp16RayDecoder vlp16RayDecoder, Vlp16PosDecoder vlp16PosDecoder) {
    LidarAngularFiringCollector lidarAngularFiringCollector = LidarAngularFiringCollector.createDefault();
    Vlp16SpacialProvider vlp16SpacialProvider = new Vlp16SpacialProvider();
    vlp16SpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    vlp16RayDecoder.addListener(vlp16SpacialProvider);
    vlp16RayDecoder.addListener(lidarRotationProvider);
    return lidarAngularFiringCollector;
  }
}
