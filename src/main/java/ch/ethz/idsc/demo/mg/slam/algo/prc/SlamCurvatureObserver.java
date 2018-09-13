// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

// observes the local curvature and since this value should be continuous, we can disregard estimated curves with changing curvature
/* package */ class SlamCurvatureObserver {
  private final SlamConfig slamConfig;
  // ---
  private Scalar lastLocalCurvature;

  SlamCurvatureObserver(SlamConfig slamConfig) {
    this.slamConfig = slamConfig;
    lastLocalCurvature = RealScalar.of(0);
  }

  public boolean curvatureContinuous(Scalar currentLocalCurvature) {
    Scalar deltaCurvatureAbs = lastLocalCurvature.subtract(currentLocalCurvature).abs();
    if (Scalars.lessEquals(deltaCurvatureAbs, slamConfig.deltaCurvatureThreshold)) {
      lastLocalCurvature = currentLocalCurvature;
      return true;
    }
    return false;
  }
}
