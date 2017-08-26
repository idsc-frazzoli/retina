// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32eUtils;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.data.Hdl32ePanoramaFrame;

/** simple visualizations of firing and positioning data on lcm for debugging */
public enum Hdl32eLcmViewer {
  ;
  public static void create(String channel) {
    Hdl32eLcmClient client = new Hdl32eLcmClient(channel);
    // ---
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    client.hdl32eRayDecoder.addListener(hdl32ePanoramaCollector);
    // ---
    Hdl32eUtils.createRayFrame(client.hdl32eRayDecoder, client.hdl32ePosDecoder);
    // ---
    client.startSubscriptions();
  }
}
