// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import java.util.Optional;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** the sausage region is defined as a region in R^2 which is closer than distanceThreshold
 * to the curve estimated by the SLAM algorithm - similar in shape to a sausage with the estimated
 * curve as centerline. The filter sets the validity field of
 * all detected way points which are not in this region to false. */
class SausageFilter implements WaypointFilterInterface {
  private final Optional<Tensor> curve;
  private final Scalar distanceThreshold;

  /** @param curve go kart frame, currently estimated by SLAM algorithm */
  public SausageFilter(Optional<Tensor> curve) {
    this.curve = curve;
    distanceThreshold = SlamPrcConfig.GLOBAL.distanceThreshold;
  }

  @Override // from WaypointFilterInterface
  public void filter(Tensor gokartWaypoints, boolean[] validities) {
    if (curve.isPresent())
      sausageAction(gokartWaypoints, validities);
  }

  /** applies the sausage filter method to the detected way points by comparing them with the current curve
   * 
   * @param gokartWaypoints detected way points
   * @param validities corresponding validities */
  private void sausageAction(Tensor gokartWaypoints, boolean[] validities) {
    Tensor curveField = curve.get();
    Tensor minDistances = Tensors.empty();
    for (int i = 0; i < gokartWaypoints.length(); ++i) {
      if (validities[i]) {
        Scalar minDistance = SausageFilterUtil.computeMinDistance(gokartWaypoints.get(i), curveField);
        minDistances.append(minDistance);
        if (Scalars.lessEquals(distanceThreshold, minDistances.Get(i)))
          validities[i] = false;
      } else
        minDistances.append(Tensors.empty());
    }
  }
}
