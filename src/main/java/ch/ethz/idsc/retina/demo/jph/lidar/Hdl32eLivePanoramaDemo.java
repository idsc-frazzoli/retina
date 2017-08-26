// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.velodyne.VelodyneStatics;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32eRayDecoder;
import ch.ethz.idsc.retina.dev.velodyne.hdl32e.Hdl32eUtils;
import ch.ethz.idsc.retina.util.io.UniversalDatagramClient;

/** displays hdl32e live data stream as depth and intensity panorama */
enum Hdl32eLivePanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32eRayDecoder hdl32eFiringPacketDecoder = new Hdl32eRayDecoder();
    Hdl32eUtils.createPanoramaDisplay(hdl32eFiringPacketDecoder);
    UniversalDatagramClient hw = new UniversalDatagramClient( //
        VelodyneStatics.RAY_DEFAULT_PORT, //
        new byte[VelodyneStatics.RAY_PACKET_LENGTH]);
    // FIXME
    // hw.addListener(hdl32eFiringPacketConsumer);
    hw.start();
  }
}
