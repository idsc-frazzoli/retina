// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.retina.util.math.SimpleRnPointcloudDistance;
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
  private final SlamPrcContainer slamPrcContainer;
  private final Scalar distanceThreshold;
  private final int validPointsThreshold;

  /** @param slamPrcContainer go kart frame, currently estimated by SLAM algorithm */
  public SausageFilter(SlamPrcContainer slamPrcContainer) {
    this.slamPrcContainer = slamPrcContainer;
    distanceThreshold = SlamPrcConfig.GLOBAL.distanceThreshold;
    validPointsThreshold = SlamPrcConfig.GLOBAL.validPointsThreshold.number().intValue();
  }

  @Override // from WaypointFilterInterface
  public void filter(Tensor gokartWaypoints, boolean[] validities) {
    if (slamPrcContainer.getCurve().isPresent() && slamPrcContainer.getCurve().get().length() >= 3) {
      Tensor curve = slamPrcContainer.getCurve().get();
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
    for (int i = 0; i < gokartWaypoints.length(); ++i)
      if (validities[i] && gokartWaypoints.get(i).Get(0).number().doubleValue() > 0) {
        Scalar minDistance = tensorScalarFunction.apply(gokartWaypoints.get(i));
        if (Scalars.lessEquals(distanceThreshold, minDistance))
          tempValidities[i] = false;
      }
    if (checkReset(tempValidities))
      System.arraycopy(tempValidities, 0, validities, 0, validities.length);
  }

  // check that we do not set everything to zero -> would be a sign that we need to "reset" curve
  private boolean checkReset(boolean[] tempValidities) {
    int counter = 0;
    for (boolean validity : tempValidities)
      if (validity)
        counter++;
    return counter >= validPointsThreshold;
  }
}
