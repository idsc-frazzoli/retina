// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eRayDecoder;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eStatics;
import ch.ethz.idsc.retina.dev.hdl32e.app.Hdl32eUtils;
import ch.ethz.idsc.retina.util.UniversalDatagramClient;

/** displays hdl32e live data stream as depth and intensity panorama */
enum Hdl32eLivePanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32eRayDecoder hdl32eFiringPacketDecoder = new Hdl32eRayDecoder();
    Hdl32eUtils.createPanoramaDisplay(hdl32eFiringPacketDecoder);
    UniversalDatagramClient hw = new UniversalDatagramClient( //
        Hdl32eStatics.RAY_DEFAULT_PORT, //
        new byte[Hdl32eStatics.RAY_PACKET_LENGTH]);
    // FIXME
    // hw.addListener(hdl32eFiringPacketConsumer);
    hw.start();
  }
}
