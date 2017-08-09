// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringPacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePositioningPacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.RealtimeFiringPacket;
import ch.ethz.idsc.retina.util.io.PcapPacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum PcapHdl32ePanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer = PanoramaUtils.createDisplay();
    hdl32eFiringPacketConsumer.addListener(new RealtimeFiringPacket(1.0));
    Hdl32ePositioningPacketConsumer hdl32ePositioningPacketConsumer = new Hdl32ePositioningPacketConsumer();
    // hdl32ePositioningPacketConsumer.addListener(new PrintHdl32ePositioningListener());
    PcapPacketConsumer packetConsumer = new Hdl32ePacketConsumer(hdl32eFiringPacketConsumer, hdl32ePositioningPacketConsumer);
    PcapParse.of(Pcap.HIGHWAY.file, packetConsumer); // blocking
  }
}
