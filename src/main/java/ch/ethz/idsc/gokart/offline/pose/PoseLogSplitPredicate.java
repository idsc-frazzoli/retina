// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.LogSplitPredicate;
import ch.ethz.idsc.tensor.Scalar;

/** considers only messages of pose channel */
public abstract class PoseLogSplitPredicate implements LogSplitPredicate {
  @Override
  public final boolean split(Scalar time, String channel, ByteBuffer byteBuffer) {
    return channel.equals(GokartLcmChannel.POSE_LIDAR) //
        && split(GokartPoseEvent.of(byteBuffer));
  }

  /** @param gokartPoseEvent
   * @return */
  protected abstract boolean split(GokartPoseEvent gokartPoseEvent);
}
