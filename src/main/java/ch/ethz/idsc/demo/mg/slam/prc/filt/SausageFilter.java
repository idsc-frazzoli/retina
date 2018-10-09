// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import java.util.Optional;

import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.owl.bot.rn.SimpleRnPointcloudDistance;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.red.Norm;

/** the sausage region is defined as a region in R^2 which is closer than distanceThreshold
 * to the curve estimated by the SLAM algorithm - similar in shape to a sausage with the estimated
 * curve as centerline. The filter sets the validity field of
 * all detected way points which are not in this region to false. */
class SausageFilter implements WaypointFilterInterface {
  private static final int MIN_LENGTH = 3;
  // ---
  private final SlamPrcContainer slamPrcContainer;
  private final Scalar distanceThreshold;
  private final int validPointsThreshold;

  /** @param slamPrcContainer go kart frame, currently estimated by SLAM algorithm */
  public SausageFilter(SlamPrcContainer slamPrcContainer) {
    this.slamPrcContainer = slamPrcContainer;
    distanceThreshold = SlamDvsConfig.eventCamera.slamPrcConfig.distanceThreshold;
    validPointsThreshold = SlamDvsConfig.eventCamera.slamPrcConfig.validPointsThreshold.number().intValue();
  }

  @Override // from WaypointFilterInterface
  public void filter(Tensor gokartWaypoints, boolean[] validities) {
    Optional<Tensor> optional = slamPrcContainer.getCurve();
    if (optional.isPresent() && MIN_LENGTH <= optional.get().length()) {
      Tensor curve = optional.get();
      sausageAction(gokartWaypoints, validities, curve);
    }
  }

  /** applies the sausage filter method to the detected way points by comparing them with the current curve
   * 
   * @param gokartWaypoints go kart frame, detected way points
   * @param validities corresponding validities
   * @param curve */
  private void sausageAction(Tensor gokartWaypoints, boolean[] validities, Tensor curve) {
    boolean[] tempValidities = validities.clone();
    TensorScalarFunction tensorScalarFunction = SimpleRnPointcloudDistance.of(curve, Norm._2);
    int index = 0;
    for (Tensor gokartWaypoint : gokartWaypoints) {
      if (validities[index] && 0 < gokartWaypoint.Get(0).number().doubleValue()) {
        Scalar minDistance = tensorScalarFunction.apply(gokartWaypoint);
        if (Scalars.lessEquals(distanceThreshold, minDistance))
          tempValidities[index] = false;
      }
      ++index;
    }
    // check that we do not set everything to zero -> would be a sign that we need to "reset" curve
    if (validPointsThreshold <= StaticHelper.filterCount(tempValidities))
      System.arraycopy(tempValidities, 0, validities, 0, validities.length);
  }
}
