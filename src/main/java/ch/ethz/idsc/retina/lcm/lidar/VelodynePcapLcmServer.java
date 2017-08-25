// code by jph
package ch.ethz.idsc.retina.lcm.lidar;

import java.util.Arrays;

import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.util.RealtimeSleeper;
import ch.ethz.idsc.retina.util.io.PcapPacketListener;

/** class is universal for HDL-32e and VLP-16 */
public class VelodynePcapLcmServer implements PcapPacketListener {
  private final BinaryBlobPublisher rayPublisher;
  private final BinaryBlobPublisher posPublisher;
  private final RealtimeSleeper realtimeSleeper;

  public VelodynePcapLcmServer(String type, String lidarId, double speed) {
    rayPublisher = new BinaryBlobPublisher(type + "." + lidarId + ".ray");
    posPublisher = new BinaryBlobPublisher(type + "." + lidarId + ".pos");
    realtimeSleeper = new RealtimeSleeper(speed);
  }

  @Override
  public void packet(int sec, int usec, byte[] data, int length) {
    realtimeSleeper.now(sec, usec);
    switch (length) {
    case 1248:
      rayPublisher.accept(Arrays.copyOfRange(data, 42, 1248), 1206);
      break;
    case 554:
      posPublisher.accept(Arrays.copyOfRange(data, 42, 554), 512);
      break;
    default:
      System.err.println("unknown length");
      break;
    }
  }
}
