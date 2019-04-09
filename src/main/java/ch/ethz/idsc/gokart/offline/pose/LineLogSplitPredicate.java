// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.sophus.group.Se2CoveringGroupElement;
import ch.ethz.idsc.sophus.group.Se2GroupElement;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

/** starting line with certain width
 * 
 * split when previous was behind line and this pose is on or in front line both within y range
 * 
 * for instance on the 20190408 {37.37[m], 42.41[m], -2.11} */
public class LineLogSplitPredicate extends PoseLogSplitPredicate {
  private final Se2CoveringGroupElement inverse;
  private final Clip clip_y;
  // ---
  private boolean fuse = false;

  /** @param linePose {x[m], y[m], alpha}
   * @param y_width with unit [m] */
  public LineLogSplitPredicate(Tensor linePose, Scalar y_width) {
    inverse = new Se2GroupElement(linePose).inverse();
    clip_y = Clips.interval(y_width.negate(), y_width);
  }

  @Override // from PoseLogSplitPredicate
  protected boolean split(GokartPoseEvent gokartPoseEvent) {
    Tensor local = inverse.combine(gokartPoseEvent.getPose());
    Scalar px = local.Get(0);
    Scalar py = local.Get(1);
    boolean result = fuse // previous position was behind line and within y range
        && Sign.isPositiveOrZero(px) // x coordinate in front of line
        && clip_y.isInside(py); // y coordinate is within bounds
    fuse = Sign.isNegative(px) //
        && clip_y.isInside(py);
    return result;
  }
}
