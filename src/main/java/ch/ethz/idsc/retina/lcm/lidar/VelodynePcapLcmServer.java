// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import ch.ethz.idsc.retina.demo.jph.hdl32e.Pcap;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.util.io.PcapPacketListener;
import ch.ethz.idsc.retina.util.io.PcapParse;

public class VelodynePcapLcmServer implements PcapPacketListener {
  final String type;
  final BinaryBlobPublisher rayPublisher;
  final BinaryBlobPublisher posPublisher;

  public VelodynePcapLcmServer(String type, String lidarId) {
    this.type = type;
    rayPublisher = new BinaryBlobPublisher(type + "." + lidarId + ".ray");
    posPublisher = new BinaryBlobPublisher(type + "." + lidarId + ".pos");
  }

  @Override
  public void packet(int sec, int usec, byte[] data, int length) {
    switch (length) {
    case 1248:
      rayPublisher.accept(data, length);
      break;
    case 554:
      posPublisher.accept(data, length);
      break;
    default:
      System.err.println("unknown length");
      break;
    }
  }

  public static void main(String[] args) throws Exception {
    VelodynePcapLcmServer server = new VelodynePcapLcmServer("hdl32e", "center");
    PcapParse.of(Pcap.HIGHWAY.file, server); // blocking
  }
}
