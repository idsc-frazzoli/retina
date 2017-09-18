// code by jph
package ch.ethz.idsc.retina.util.io;

import java.io.File;

/** demo exists in case inspection of a pcap file is useful */
enum PcapParseDemo {
  ;
  public static void main(String[] args) throws Exception {
    PcapPacketListener pcapPacketListener = new PcapPacketListener() {
      @Override
      public void pcapPacket(int sec, int usec, byte[] packet_data, int length) {
        System.out.println(sec + " " + usec + " " + length);
      }
    };
    PcapParse.of(new File("somefile.pcap"), pcapPacketListener);
  }
}
