// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.channel.SingleChannelInterface;
import ch.ethz.idsc.tensor.Tensor;

public enum GokartPosePostChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override // from SingleChannelTable
  public String channel() {
    return "gokart.pose.post";
  }

  @Override // from SingleChannelTable
  public Tensor row(ByteBuffer byteBuffer) {
    return GokartPoseChannel.INSTANCE.row(byteBuffer);
  }
}
