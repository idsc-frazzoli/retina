// code by jph
package ch.ethz.idsc.retina.demo.jph.lidar;

import ch.ethz.idsc.retina.dev.velodyne.vlp16.Vlp16PacketProvider;
import ch.ethz.idsc.retina.dev.velodyne.vlp16.Vlp16SpacialProvider;
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
    Vlp16PacketProvider vlp16PacketProvider = new Vlp16PacketProvider();
    Vlp16SpacialProvider vlp16SpacialProvider = new Vlp16SpacialProvider();
    vlp16PacketProvider.vlp16Decoder.addRayListener(vlp16SpacialProvider);
    // packetConsumer.vlp16PosDecoder.addListener(null);
    PcapParse.of(Vlp16Pcap.DOWNTOWN_SINGLE.file, vlp16PacketProvider);
    // System.out.println(String.format("%02x", 55));
  }
}
