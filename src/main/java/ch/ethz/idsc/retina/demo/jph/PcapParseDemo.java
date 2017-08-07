// code by jph
package ch.ethz.idsc.retina.demo.jph;

import java.io.File;

import ch.ethz.idsc.retina.util.io.PacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

enum PcapParseDemo {
  ;
  public static void main(String[] args) throws Exception {
    PacketConsumer packetConsumer = new PacketConsumer() {
      @Override
      public void parse(byte[] packet_data, int length) {
      }
    };
    String dir = "/media/datahaki/media/ethz/sensors/velodyne01/usb/Velodyne/HDL-32E Sample Data";
    String name = "HDL32-V2_Tunnel.pcap";
    new PcapParse(new File(dir, name), packetConsumer);
  }
}
