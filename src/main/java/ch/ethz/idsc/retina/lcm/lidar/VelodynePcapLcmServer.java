// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.util.Arrays;

import ch.ethz.idsc.retina.dev.hdl32e.VelodyneModel;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.util.RealtimeSleeper;
import ch.ethz.idsc.retina.util.io.PcapPacketListener;

/** class is universal for HDL-32e and VLP-16 */
public class VelodynePcapLcmServer implements PcapPacketListener {
  private final BinaryBlobPublisher rayPublisher;
  private final BinaryBlobPublisher posPublisher;
  private final RealtimeSleeper realtimeSleeper;

  public VelodynePcapLcmServer(VelodyneModel velodyneModel, String lidarId, double speed) {
    rayPublisher = new BinaryBlobPublisher(VelodyneLcmChannels.ray(velodyneModel, lidarId));
    posPublisher = new BinaryBlobPublisher(VelodyneLcmChannels.pos(velodyneModel, lidarId));
    realtimeSleeper = new RealtimeSleeper(speed);
  }

  @Override
  public void packet(int sec, int usec, byte[] data, int length) {
    realtimeSleeper.now(sec, usec);
    switch (length) {
    case 1248: // length of ray packet in pcap file
      rayPublisher.accept(Arrays.copyOfRange(data, 42, 1248), 1206); // length of ray packet
      break;
    case 554:
      posPublisher.accept(Arrays.copyOfRange(data, 42, 554), 512); // length of pos packet
      break;
    default:
      System.err.println("unknown length");
      break;
    }
  }
}
