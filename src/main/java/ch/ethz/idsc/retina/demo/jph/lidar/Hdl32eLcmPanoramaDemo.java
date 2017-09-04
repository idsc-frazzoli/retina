// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.app.LidarPanoramaFrame;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmClient;

/** displays hdl32e live data stream as depth and intensity panorama */
enum Hdl32eLcmPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    // Hdl32eDecoder hdl32eDecoder = new Hdl32eDecoder();
    VelodyneLcmClient vlc = VelodyneLcmClient.hdl32e("center");
    LidarPanoramaFrame hdl32ePanoramaFrame = new LidarPanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    vlc.velodyneDecoder.addRayListener(hdl32ePanoramaCollector);
    vlc.startSubscriptions();
    // UniversalDatagramClient hw = new UniversalDatagramClient( //
    // VelodyneStatics.RAY_DEFAULT_PORT, //
    // new byte[VelodyneStatics.RAY_PACKET_LENGTH]);
    // // FIXME
    // // hw.addListener(hdl32eFiringPacketConsumer);
    // hw.start();
  }
}
