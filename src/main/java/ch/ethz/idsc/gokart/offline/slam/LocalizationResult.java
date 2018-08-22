// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// TODO JPH limit visibility
public class LocalizationResult {
  public final Scalar time;
  public final Tensor pose_xyt; // {x, y, theta} without units
  public final Scalar ratio;

  public LocalizationResult(Scalar time, Tensor pose_xyt, Scalar ratio) {
    this.time = time;
    this.pose_xyt = pose_xyt.unmodifiable();
    this.ratio = ratio;
  }

  public GokartPoseEvent getGokartPoseEvent() {
    // TODO check units!
    return GokartPoseEvents.getPoseEvent( //
        GokartPoseHelper.attachUnits(pose_xyt), //
        ratio);
  }
}
