// code by jph
package ch.ethz.idsc.retina.lcm.davis;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.data.DavisApsBlockListener;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

class DavisApsBlockPublisher implements DavisApsBlockListener {
  /** @param serial
   * @return aps channel name for given serial number of davis camera */
  public static String channel(String serial) {
    return "davis." + serial + ".aps";
  }

  // ---
  private final LCM lcm = LCM.getSingleton();
  private final String channel;

  public DavisApsBlockPublisher(String serial) {
    channel = channel(serial);
  }

  @Override
  public void apsBlock(int length, ByteBuffer byteBuffer) {
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data_length = length;
    binaryBlob.data = new byte[length]; // TODO try assigning byte buf array
    byteBuffer.get(binaryBlob.data);
    lcm.publish(channel, binaryBlob);
  }
}
