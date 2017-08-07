// code by jph
package ch.ethz.idsc.retina.demo.jph;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePacketConsumer;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanorama;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaCollector;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePanoramaListener;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePositionListener;
import ch.ethz.idsc.retina.util.io.PacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

/** collects array of 3d positions */
enum Hdl32ePacketConsumerDemo {
  ;
  public static void main(String[] args) throws Exception {
    @SuppressWarnings("unused")
    Hdl32ePositionListener hdl32ePositionListener = new Hdl32ePositionListener() {
      @Override
      public void digest(float[] position_data, int length) {
        // System.out.println("here");
      }
    };
    Hdl32ePanoramaListener hdl32ePanoramaListener = new Hdl32ePanoramaListener() {
      @Override
      public void panorama(Hdl32ePanorama hdl32ePanorama) {
        // System.out.println(hdl32ePanorama.angle);
      }

      @Override
      public void close() {
        // ---
      }
    };
    PacketConsumer packetConsumer = new Hdl32ePacketConsumer( //
        // new Hdl32ePositionCollector(hdl32ePositionListener) //
        new Hdl32ePanoramaCollector(hdl32ePanoramaListener) //
    );
    new PcapParse(Pcap.TUNNEL.file, packetConsumer);
  }
}