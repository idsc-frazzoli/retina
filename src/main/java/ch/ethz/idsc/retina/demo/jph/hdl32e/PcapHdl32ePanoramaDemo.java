// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import java.io.File;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringPacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaFrame;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaListener;
import ch.ethz.idsc.retina.dev.hdl32e.RealtimeFiringPacket;
import ch.ethz.idsc.retina.util.io.PacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum PcapHdl32ePanoramaDemo {
  ;
  static void simple(File file, Hdl32ePanoramaListener hdl32ePanoramaListener) throws Exception {
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaListener);
    Hdl32eFiringPacketConsumer hdl32eFiringPacketConsumer = new Hdl32eFiringPacketConsumer();
    hdl32eFiringPacketConsumer.addListener(hdl32ePanoramaCollector);
    hdl32eFiringPacketConsumer.addListener(new RealtimeFiringPacket(1.0));
    PacketConsumer packetConsumer = new Hdl32ePacketConsumer(hdl32eFiringPacketConsumer);
    PcapParse.of(file, packetConsumer); // blocking
    hdl32ePanoramaListener.close();
  }

  public static void main(String[] args) throws Exception {
    simple(Pcap.HIGHWAY.file, new Hdl32ePanoramaFrame());
    // hdl32ePanoramaListener = new Hdl32ePanoramaWriter(UserHome.Pictures("distances.gif"), 100, 640);
  }
}
