// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import ch.ethz.idsc.retina.dev.velodyne.app.VelodyneUtils;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaFrame;

/** simple visualizations of firing and positioning data on lcm for debugging */
public enum Hdl32eLcmViewer {
  ;
  public static void create(String channel) {
    VelodyneLcmClient client = VelodyneLcmClient.hdl32e(channel);
    // ---
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    client.velodyneDecoder.addRayListener(hdl32ePanoramaCollector);
    // ---
    VelodyneUtils.createRayFrame( //
        VelodyneUtils.createCollector32(client.velodyneDecoder), client.velodyneDecoder);
    // ---
    client.startSubscriptions();
  }
}
