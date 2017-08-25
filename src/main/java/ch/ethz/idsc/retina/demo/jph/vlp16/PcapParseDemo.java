// code by jph
package ch.ethz.idsc.retina.demo.jph.vlp16;

import java.io.File;

import ch.ethz.idsc.retina.dev.vlp16.Vlp16PacketProvider;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum PcapParseDemo {
  ;
  public static void main(String[] args) throws Exception {
    ByteArrayConsumer byteArrayConsumer = new ByteArrayConsumer() {
      @Override
      public void accept(byte[] packet_data, int length) {
        System.out.println("" + length);
      }
    };
    Vlp16PacketProvider packetConsumer = new Vlp16PacketProvider();
    File file = new File("/media/datahaki/media/ethz/vlp16/VELODYNE/VLP-16 Sample Data", //
        "2015-07-23-14-37-22_Velodyne-VLP-16-Data_Downtown 10Hz Single.pcap");
    PcapParse.of(file, packetConsumer);
  }
}
