// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringListener;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringPacketDecoder;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePositioningPacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32ePanorama;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.hdl32e.data.Hdl32ePanoramaListener;
import ch.ethz.idsc.retina.util.io.PcapPacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum Hdl32ePacketConsumerDemo {
  ;
  public static void main(String[] args) throws Exception {
    @SuppressWarnings("unused")
    Hdl32eFiringListener hdl32ePositionListener = new Hdl32eFiringListener() {
      @Override
      public void digest(float[] position_data, int length) {
        System.out.println("here");
      }
    };
    Hdl32ePanoramaListener hdl32ePanoramaListener = new Hdl32ePanoramaListener() {
      @Override
      public void panorama(Hdl32ePanorama hdl32ePanorama) {
        // System.out.println("here");
      }

      @Override
      public void close() {
        // ---
      }
    };
    Hdl32ePanoramaCollector hdl32ePanoramaCollector = new Hdl32ePanoramaCollector();
    hdl32ePanoramaCollector.addListener(hdl32ePanoramaListener);
    Hdl32eFiringPacketDecoder hdl32eFiringPacketConsumer = new Hdl32eFiringPacketDecoder();
    hdl32eFiringPacketConsumer.addListener(hdl32ePanoramaCollector);
    Hdl32ePositioningPacketConsumer hdl32eGpsPacketConsumer = new Hdl32ePositioningPacketConsumer();
    PcapPacketConsumer packetConsumer = new Hdl32ePacketConsumer(hdl32eFiringPacketConsumer, hdl32eGpsPacketConsumer);
    PcapParse.of(Pcap.TUNNEL.file, packetConsumer);
  }
}
