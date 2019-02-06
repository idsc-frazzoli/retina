// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.tensor.Tensor;

public class GokartPoseChannel implements SingleChannelInterface {
  @Override // from SingleChannelTable
  public String channel() {
    return GokartLcmChannel.POSE_LIDAR;
  }

  @Override // from SingleChannelTable
  public Tensor row(ByteBuffer byteBuffer) {
    GokartPoseEvent gokartPoseEvent = new GokartPoseEvent(byteBuffer);
    return GokartPoseHelper.toUnitless(gokartPoseEvent.getPose()).append(gokartPoseEvent.getQuality());
  }
}
