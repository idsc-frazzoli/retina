// code by mh, jph
package ch.ethz.idsc.gokart.offline.pose;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Sign;

public class LapLogSplitPredicate extends PoseLogSplitPredicate {
  private final Tensor standartPosition;
  private final Tensor standartDirection;
  private Tensor pose_prev;

  public LapLogSplitPredicate(Tensor standartPosition, Tensor standartDirection) {
    this.standartPosition = standartPosition;
    this.standartDirection = standartDirection;
  }

  @Override // from PoseLogSplitPredicate
  protected boolean split(GokartPoseEvent gokartPoseEvent) {
    Tensor pose_next = gokartPoseEvent.getPose();
    boolean split = getLineTrigger(pose_prev.extract(0, 2), pose_next.extract(0, 2));
    pose_prev = pose_next;
    return split;
  }

  private boolean isInFront(Tensor position) {
    Tensor diff = position.subtract(standartPosition);
    Scalar normalDistance = (Scalar) diff.dot(standartDirection);
    return Sign.isPositive(normalDistance);
  }

  public boolean getLineTrigger(Tensor lastPosition, Tensor position) {
    return !isInFront(lastPosition) && isInFront(position);
  }
}
