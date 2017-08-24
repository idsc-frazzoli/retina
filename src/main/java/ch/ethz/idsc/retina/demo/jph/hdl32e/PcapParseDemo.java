// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum PcapParseDemo {
  ;
  public static void main(String[] args) throws Exception {
    ByteArrayConsumer packetConsumer = new ByteArrayConsumer() {
      @Override
      public void accept(byte[] packet_data, int length) {
        System.out.println("" + length);
      }
    };
    PcapParse.of(Pcap.TUNNEL.file, packetConsumer);
  }
}
