// code by jph
package ch.ethz.idsc.gokart.lcm.lidar;

import java.util.Arrays;

import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;
import ch.ethz.idsc.retina.util.io.PcapPacketListener;

/** class is universal for HDL-32e and VLP-16 */
public class VelodynePcapLcmServer implements PcapPacketListener {
  private final BinaryBlobPublisher rayPublisher;
  private final BinaryBlobPublisher posPublisher;

  public VelodynePcapLcmServer(VelodyneModel velodyneModel, String lidarId) {
    rayPublisher = new BinaryBlobPublisher(VelodyneLcmChannels.ray(velodyneModel, lidarId));
    posPublisher = new BinaryBlobPublisher(VelodyneLcmChannels.pos(velodyneModel, lidarId));
  }

  @Override
  public void pcapPacket(int sec, int usec, byte[] data, int length) {
    switch (length) {
    case 1248: // length of ray packet in pcap file
      rayPublisher.accept(Arrays.copyOfRange(data, 42, 1248), VelodyneStatics.RAY_PACKET_LENGTH);
      break;
    case 554:
      posPublisher.accept(Arrays.copyOfRange(data, 42, 554), VelodyneStatics.POS_PACKET_LENGTH);
      break;
    default:
      System.err.println("unknown length");
      break;
    }
  }
}
