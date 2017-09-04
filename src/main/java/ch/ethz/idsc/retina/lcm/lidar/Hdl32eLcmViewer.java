// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaFrame;
import ch.ethz.idsc.retina.dev.lidar.app.VelodyneUtils;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32ePanoramaProvider;

/** simple visualizations of firing and positioning data on lcm for debugging */
public enum Hdl32eLcmViewer {
  ;
  public static void create(String channel) {
    VelodyneLcmClient client = VelodyneLcmClient.hdl32e(channel);
    // ---
    LidarPanoramaFrame hdl32ePanoramaFrame = new LidarPanoramaFrame();
    Hdl32ePanoramaProvider hdl32ePanoramaCollector = new Hdl32ePanoramaProvider();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    client.velodyneDecoder.addRayListener(hdl32ePanoramaCollector);
    // ---
    VelodyneUtils.createRayFrame( //
        VelodyneUtils.createCollector32(client.velodyneDecoder), client.velodyneDecoder);
    // ---
    client.startSubscriptions();
  }
}
