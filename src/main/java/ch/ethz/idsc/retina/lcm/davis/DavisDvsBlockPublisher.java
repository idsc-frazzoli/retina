// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.data.DavisDvsBlockListener;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

class DavisDvsBlockPublisher implements DavisDvsBlockListener {
  /** @param cameraId
   * @return dvs channel name for given serial number of davis camera */
  public static String channel(String cameraId) {
    return DavisLcmStatics.CHANNEL_PREFIX + "." + cameraId + ".dvs";
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
    binaryBlob.data = new byte[length]; // TODO try assigning byte buf array
    byteBuffer.get(binaryBlob.data);
    lcm.publish(channel, binaryBlob);
  }
}
