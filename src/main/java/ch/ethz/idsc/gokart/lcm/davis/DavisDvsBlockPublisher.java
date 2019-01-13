// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.retina.davis.data.DavisDvsBlockListener;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public class DavisDvsBlockPublisher implements DavisDvsBlockListener {
  /** @param cameraId
   * @return dvs channel name for given serial number of davis camera */
  public static String channel(String cameraId) {
    Objects.requireNonNull(cameraId);
    return DavisLcmStatics.CHANNEL_PREFIX + "." + cameraId + DavisLcmChannel.DVS.extension;
  }

  // ---
  private final LCM lcm = LCM.getSingleton();
  private final String channel;

  public DavisDvsBlockPublisher(String cameraId) {
    channel = channel(cameraId);
  }

  @Override
  public void dvsBlock(int length, ByteBuffer byteBuffer) {
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data_length = length;
    binaryBlob.data = new byte[length];
    byteBuffer.get(binaryBlob.data);
    lcm.publish(channel, binaryBlob);
  }
}
