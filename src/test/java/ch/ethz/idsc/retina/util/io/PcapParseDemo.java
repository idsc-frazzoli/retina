// code by jph
package ch.ethz.idsc.retina.util.io;

import java.io.File;

enum PcapParseDemo {
  ;
  public static void main(String[] args) throws Exception {
    PcapPacketListener pcapPacketListener = new PcapPacketListener() {
      @Override
      public void packet(int sec, int usec, byte[] packet_data, int length) {
        System.out.println("" + length);
      }
    };
    PcapParse.of(new File("somefile.pcap"), pcapPacketListener); // TODO
  }
}
