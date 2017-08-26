// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePcapPacketDecoder;
import ch.ethz.idsc.retina.dev.hdl32e.app.Hdl32eUtils;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum Hdl32ePcapFiringDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32ePcapPacketDecoder hdl32ePacketProvider = new Hdl32ePcapPacketDecoder(1);
    Hdl32eUtils.createRayFrame(hdl32ePacketProvider.hdl32eRayDecoder, hdl32ePacketProvider.hdl32ePosDecoder);
    // ---
    PcapParse.of(Hdl32ePcap.HIGHWAY.file, hdl32ePacketProvider); // blocking
  }
}
