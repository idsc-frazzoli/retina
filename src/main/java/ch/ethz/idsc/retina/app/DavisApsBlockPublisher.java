// code by jph
package ch.ethz.idsc.retina.app;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.davis.data.DavisApsBlockListener;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public class DavisApsBlockPublisher implements DavisApsBlockListener {
  public static final String APS_CHANNEL = "davis.id.aps";
  // ---
  private final LCM lcm = LCM.getSingleton();

  @Override
  public void apsBlock(int length, ByteBuffer byteBuffer) {
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data_length = length;
    binaryBlob.data = new byte[length]; // TODO try assigning byte buf array
    byteBuffer.get(binaryBlob.data);
    lcm.publish(APS_CHANNEL, binaryBlob);
  }
}
