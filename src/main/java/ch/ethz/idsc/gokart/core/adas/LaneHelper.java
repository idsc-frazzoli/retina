// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.sophus.hs.r2.Se2ParametricDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum LaneHelper {
  ;
  /** function is used to determine whether the gokart has left the lane */
  public static boolean isOutside(Optional<Tensor> optionalCurve, GokartPoseEvent gokartPoseEvent, Scalar criticalDistance) {
    if (optionalCurve.isPresent() && Objects.nonNull(gokartPoseEvent)) {
      Tensor pose = gokartPoseEvent.getPose(); // of the form {x[m], y[m], heading}
      Tensor curve = optionalCurve.get();
      int index = Se2CurveHelper.closest(curve, pose); // closest gives the index of the closest element
      Tensor closest = curve.get(index);
      Scalar currDistance = Se2ParametricDistance.INSTANCE.distance(closest, pose);
      return Scalars.lessThan(criticalDistance, currDistance);
    }
    return false;
  }
}
