// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePacketProvider;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eRealtimeFiringPacket;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32ePanoramaFrame;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum Hdl32ePcapPanoramaDemo {
  ;
  public static void main(String[] args) throws Exception {
    Hdl32ePanoramaFrame hdl32ePanoramaFrame = new Hdl32ePanoramaFrame();
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaFrame);
    Hdl32ePacketProvider packetConsumer = new Hdl32ePacketProvider();
    packetConsumer.hdl32eRayDecoder.addListener(hdl32ePanoramaCollector);
    packetConsumer.hdl32eRayDecoder.addListener(new Hdl32eRealtimeFiringPacket(1.0));
    packetConsumer.hdl32ePosDecoder.addListener(hdl32ePanoramaFrame);
    PcapParse.of(Pcap.HIGHWAY.file, packetConsumer); // blocking
  }
}
