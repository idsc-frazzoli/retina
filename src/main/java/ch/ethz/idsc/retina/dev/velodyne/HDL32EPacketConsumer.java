// code by jph
package ch.ethz.idsc.retina.dev.velodyne;

import java.io.File;

import ch.ethz.idsc.retina.util.io.PacketConsumer;
import ch.ethz.idsc.retina.util.io.PcapParse;

public class HDL32EPacketConsumer implements PacketConsumer {
  public static final int LASER_SIZE = 1248;
  // ---
  private final HDL32EFiringPacketConsumer firingPacketConsumer;

  public HDL32EPacketConsumer(HDL32EFiringPacketConsumer firingPacketConsumer) {
    this.firingPacketConsumer = firingPacketConsumer;
  }

  @Override
  public void parse(byte[] packet_data, int length) {
    if (length == LASER_SIZE) {
      firingPacketConsumer.lasers(packet_data);
    } else {
      // 554 for GPS (?)
      // System.out.println(length);
    }
  }

  public static void main(String[] args) throws Exception {
    LaserPositionConsumer laserPositionConsumer = new LaserPositionConsumer() {
      @Override
      public void digest(float[] position_data, int length) {
        // System.out.println("here");
      }
    };
    PacketConsumer packetConsumer = new HDL32EPacketConsumer( //
        new HDL32EFiringCollector(laserPositionConsumer));
    new PcapParse(
        new File( //
            "/media/datahaki/media/ethz/sensors/velodyne01/usb/Velodyne/HDL-32E Sample Data", //
            "HDL32-V2_Tunnel.pcap"), //
        packetConsumer);
  }
}
