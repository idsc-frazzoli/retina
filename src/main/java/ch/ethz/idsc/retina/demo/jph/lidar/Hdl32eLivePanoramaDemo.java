// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.dev.lidar.app.VelodyneUtils;
import ch.ethz.idsc.retina.dev.lidar.hdl32e.Hdl32eDecoder;
import ch.ethz.idsc.retina.util.io.UniversalDatagramClient;

/** displays hdl32e live data stream as depth and intensity panorama */
enum Hdl32eLivePanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32eDecoder hdl32eFiringPacketDecoder = new Hdl32eDecoder();
    VelodyneUtils.createPanoramaDisplay(hdl32eFiringPacketDecoder);
    UniversalDatagramClient hw = new UniversalDatagramClient( //
        VelodyneStatics.RAY_DEFAULT_PORT, //
        new byte[VelodyneStatics.RAY_PACKET_LENGTH]);
    // FIXME
    // hw.addListener(hdl32eFiringPacketConsumer);
    hw.start();
  }
}
