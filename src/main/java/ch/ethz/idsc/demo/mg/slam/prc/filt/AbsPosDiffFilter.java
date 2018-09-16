// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm2Squared;

/** filters way points that are too close to previous valid way point */
/* package */ class AbsPosDiffFilter implements WaypointCompareInterface {
  private final Scalar deltaPosThreshold = SlamPrcConfig.GLOBAL.deltaPosThreshold;

  @Override // from WaypointFilterInterface
  public boolean filter(Tensor currentPoint, Tensor previousValidPoint) {
    Scalar posDist = Norm2Squared.ofVector(currentPoint.subtract(previousValidPoint));
    if (Scalars.lessEquals(deltaPosThreshold.multiply(deltaPosThreshold), posDist))
      return true;
    return false;
  }
}
