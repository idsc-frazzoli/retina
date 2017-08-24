// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringDecoder;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eLiveFiringClient;

/** displays hdl32e live data stream as depth and intensity panorama */
enum Hdl32eLivePanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32eFiringDecoder hdl32eFiringPacketDecoder = new Hdl32eFiringDecoder();
    Utils.createPanoramaDisplay(hdl32eFiringPacketDecoder);
    Hdl32eLiveFiringClient hw = new Hdl32eLiveFiringClient();
    // FIXME
    // hw.addListener(hdl32eFiringPacketConsumer);
    hw.start();
  }
}
