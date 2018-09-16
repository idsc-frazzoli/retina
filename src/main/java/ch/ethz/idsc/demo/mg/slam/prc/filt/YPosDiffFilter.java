// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/** filters away points that have a too large discrepancy in the y coordinate from previous valid way point */
/* package */ class YPosDiffFilter implements WaypointCompareInterface {
  private final Scalar deltaYThreshold = SlamPrcConfig.GLOBAL.deltaYThreshold;

  @Override // from SlamWaypointFilterInterface
  public boolean filter(Tensor currentPoint, Tensor previousValidPoint) {
    Scalar deltaY = currentPoint.Get(1).subtract(previousValidPoint.Get(1)).abs();
    if (Scalars.lessEquals(deltaY, deltaYThreshold))
      return true;
    return false;
  }
}
