// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Round;

public enum GokartPoseChannel implements SingleChannelInterface {
  INSTANCE;
  // ---
  @Override // from SingleChannelTable
  public String channel() {
    return GokartLcmChannel.POSE_LIDAR;
  }

  @Override // from SingleChannelTable
  public Tensor row(ByteBuffer byteBuffer) {
    GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
    return GokartPoseHelper.toUnitless(gokartPoseEvent.getPose()).map(Round._6).append(gokartPoseEvent.getQuality().map(Round._3));
  }
}
