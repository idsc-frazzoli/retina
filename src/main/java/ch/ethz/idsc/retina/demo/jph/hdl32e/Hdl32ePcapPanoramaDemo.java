// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.hdl32e.Hdl32eFiringPacketConsumer;
import ch.ethz.idsc.retina.hdl32e.Hdl32ePacketConsumer;
import ch.ethz.idsc.retina.hdl32e.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.hdl32e.Hdl32ePanoramaFrame;
import ch.ethz.idsc.retina.hdl32e.Hdl32ePositioningPacketConsumer;
import ch.ethz.idsc.retina.hdl32e.Hdl32eRealtimeFiringPacket;
import ch.ethz.idsc.retina.util.io.PcapPacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum Hdl32ePcapPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer = new Hdl32eFiringPacketConsumer();
    hdl32eFiringPacketConsumer.addListener(hdl32ePanoramaCollector);
    hdl32eFiringPacketConsumer.addListener(new Hdl32eRealtimeFiringPacket(1.0));
    Hdl32ePositioningPacketConsumer hdl32ePositioningPacketConsumer = new Hdl32ePositioningPacketConsumer();
    hdl32ePositioningPacketConsumer.addListener(hdl32ePanoramaFrame);
    PcapPacketConsumer packetConsumer = new Hdl32ePacketConsumer(hdl32eFiringPacketConsumer, hdl32ePositioningPacketConsumer);
    PcapParse.of(Pcap.HIGHWAY.file, packetConsumer); // blocking
  }
}
