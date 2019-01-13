// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import ch.ethz.idsc.retina.lidar.app.VelodynePcapPacketListener;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SpacialProvider;
import ch.ethz.idsc.retina.util.io.ByteArrayConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum PcapParseDemo {
  ;
  public static void main(String[] args) throws Exception {
    @SuppressWarnings("unused")
    ByteArrayConsumer byteArrayConsumer = new ByteArrayConsumer() {
      @Override
      public void accept(byte[] packet_data, int length) {
        System.out.println(Integer.toString(length));
      }
    };
    VelodynePcapPacketListener velodynePcapPacketListener = VelodynePcapPacketListener.vlp16();
    Vlp16SpacialProvider vlp16SpacialProvider = new Vlp16SpacialProvider(0.0);
    velodynePcapPacketListener.velodyneDecoder.addRayListener(vlp16SpacialProvider);
    PcapParse.of(Vlp16Pcap.DOWNTOWN_SINGLE.file, velodynePcapPacketListener);
  }
}
