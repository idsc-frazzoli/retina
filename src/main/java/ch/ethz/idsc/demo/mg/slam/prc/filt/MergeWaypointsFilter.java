// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.AbsSquared;

/** filters way points that are too close to previous valid way point */
/* package */ class MergeWaypointsFilter implements WaypointFilterInterface {
  private final Scalar deltaPosThresholdSquared = AbsSquared.FUNCTION.apply(SlamPrcConfig.GLOBAL.deltaPosThreshold);

  @Override // from WaypointFilterInterface
  public void filter(Tensor gokartWaypoints, boolean[] validities) {
    int firstValidIndex = findFirstValidIndex(validities);
    filterWaypoints(gokartWaypoints, validities, firstValidIndex);
  }

  /** @param validities
   * @return in case no point is valid, validities.length is returned */
  private static int findFirstValidIndex(boolean[] validities) {
    for (int i = 0; i < validities.length; ++i)
      if (validities[i])
        return i;
    return validities.length;
  }

  /** filters the ordered list of way points by iterating through it and comparing the current way point with the last valid one
   * 
   * @param gokartWaypoints go kart frame
   * @param validities same length as gokartWaypoints
   * @param firstValidIndex */
  private void filterWaypoints(Tensor gokartWaypoints, boolean[] validities, int firstValidIndex) {
    int previousValidIndex = firstValidIndex;
    for (int i = firstValidIndex + 1; i < gokartWaypoints.length(); ++i)
      if (validities[i]) {
        if (filterCondition(gokartWaypoints.get(i), gokartWaypoints.get(previousValidIndex)))
          previousValidIndex = i;
        else
          validities[i] = false;
      }
  }

  /** @param currentPoint go kart frame
   * @param previousValidPoint go kart frame
   * @return true when distance between points is larger than deltaPosThreshold */
  private boolean filterCondition(Tensor currentPoint, Tensor previousValidPoint) {
    return Scalars.lessEquals( //
        deltaPosThresholdSquared, //
        Norm2Squared.between(currentPoint, previousValidPoint));
  }
}
