// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePacketProvider;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum Hdl32ePcapFiringDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32ePacketProvider packetConsumer = new Hdl32ePacketProvider();
    Utils.createFiringFrame(packetConsumer.hdl32eFiringDecoder, packetConsumer.hdl32ePositioningDecoder);
    // ---
    PcapParse.of(Pcap.HIGHWAY.file, packetConsumer); // blocking
  }
}
