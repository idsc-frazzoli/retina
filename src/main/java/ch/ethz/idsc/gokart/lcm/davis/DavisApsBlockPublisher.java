// code by jph
package ch.ethz.idsc.gokart.lcm.davis;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.davis.DavisApsType;
import ch.ethz.idsc.retina.davis.data.DavisApsBlockListener;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public class DavisApsBlockPublisher implements DavisApsBlockListener {
  /** @param cameraId
   * @param davisApsType
   * @return channel name for given id */
  public static String channel(String cameraId, DavisApsType davisApsType) {
    return DavisLcmStatics.CHANNEL_PREFIX + "." + cameraId + davisApsType.extension;
  }

  // ---
  private final LCM lcm = LCM.getSingleton();
  private final String channel;

  public DavisApsBlockPublisher(String cameraId, DavisApsType davisApsType) {
    channel = channel(cameraId, davisApsType);
  }

  @Override // from DavisApsBlockListener
  public void apsBlock(ByteBuffer byteBuffer) {
    final int length = byteBuffer.remaining();
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data_length = length;
    // TODO JPH try assigning byte buffer array, also in DavisDvsBlockPublisher
    binaryBlob.data = new byte[length];
    byteBuffer.get(binaryBlob.data);
    lcm.publish(channel, binaryBlob);
  }
}
