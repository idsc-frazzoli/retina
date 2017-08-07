// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.util.io.PacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum PcapParseDemo {
  ;
  public static void main(String[] args) throws Exception {
    PacketConsumer packetConsumer = new PacketConsumer() {
      @Override
      public void parse(byte[] packet_data, int length) {
        System.out.println("" + length);
      }
    };
    new PcapParse(Pcap.TUNNEL.file, packetConsumer);
  }
}
