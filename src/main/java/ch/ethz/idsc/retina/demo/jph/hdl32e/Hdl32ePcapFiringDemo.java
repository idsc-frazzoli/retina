// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePacketProvider;
import ch.ethz.idsc.retina.dev.hdl32e.app.Hdl32eUtils;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum Hdl32ePcapFiringDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32ePacketProvider packetConsumer = new Hdl32ePacketProvider();
    Hdl32eUtils.createRayFrame(packetConsumer.hdl32eRayDecoder, packetConsumer.hdl32ePosDecoder);
    // ---
    PcapParse.of(Pcap.HIGHWAY.file, packetConsumer); // blocking
  }
}
