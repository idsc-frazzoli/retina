// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

public abstract class OfflineFollowingError extends FollowingError implements OfflineLogListener {
  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR) && Tensors.nonEmpty(getReference())) {
      GokartPoseEvent gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      insert(time, gokartPoseEvent.getPose());
    }
  }
}
