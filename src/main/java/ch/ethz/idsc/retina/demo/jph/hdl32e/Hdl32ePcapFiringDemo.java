// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.hdl32e.Hdl32eFiringCollector;
import ch.ethz.idsc.retina.hdl32e.Hdl32eFiringFrame;
import ch.ethz.idsc.retina.hdl32e.Hdl32eFiringPacketConsumer;
import ch.ethz.idsc.retina.hdl32e.Hdl32ePacketConsumer;
import ch.ethz.idsc.retina.hdl32e.Hdl32eRealtimeFiringPacket;
import ch.ethz.idsc.retina.util.io.PcapPacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum Hdl32ePcapFiringDemo {
  ;
  public static void main(String[] args) throws Exception {
    // Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32eFiringFrame hdl32eFiringFrame = new Hdl32eFiringFrame();
    Hdl32eFiringCollector hdl32eFiringCollector = new Hdl32eFiringCollector(hdl32eFiringFrame);
    Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer = new Hdl32eFiringPacketConsumer();
    hdl32eFiringPacketConsumer.addListener(hdl32eFiringCollector);
    hdl32eFiringPacketConsumer.addListener(new Hdl32eRealtimeFiringPacket(1.0));
    // Hdl32ePositioningPacketConsumer hdl32ePositioningPacketConsumer = new Hdl32ePositioningPacketConsumer();
    // hdl32ePositioningPacketConsumer.addListener(hdl32ePanoramaFrame);
    PcapPacketConsumer packetConsumer = new Hdl32ePacketConsumer(hdl32eFiringPacketConsumer, null);
    PcapParse.of(Pcap.HIGHWAY.file, packetConsumer); // blocking
  }
}
