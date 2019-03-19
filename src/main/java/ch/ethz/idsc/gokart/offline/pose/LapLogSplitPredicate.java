// code by mh
package ch.ethz.idsc.gokart.offline.pose;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.lcm.LogSplitPredicate;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public class LapLogSplitPredicate implements LogSplitPredicate {
  private final Tensor standartPosition;
  private final Tensor standartDirection;
  private Tensor pose_prev;

  public LapLogSplitPredicate(Tensor standartPosition, Tensor standartDirection) {
    this.standartPosition = standartPosition;
    this.standartDirection = standartDirection;
  }

  @Override // from LogSplitPredicate
  public boolean split(Scalar time, String channel, ByteBuffer byteBuffer) {
    Tensor pose_next = new GokartPoseEvent(byteBuffer).getPose();
    boolean split = getLineTrigger(pose_prev.extract(0, 2), pose_next.extract(0, 2));
    pose_prev = pose_next;
    return split;
  }

  private boolean isInFront(Tensor position) {
    Tensor diff = position.subtract(standartPosition);
    Scalar normalDistance = (Scalar) diff.dot(standartDirection);
    return Scalars.lessThan(Quantity.of(0, SI.METER), normalDistance);
  }

  public boolean getLineTrigger(Tensor lastPosition, Tensor position) {
    return !isInFront(lastPosition) && isInFront(position);
  }
}
