// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;

// observes the local curvature and since this value should be continuous, we can disregard estimated curves with changing curvature
/* package */ class SlamCurvatureObserver {
  //
  private final SlamConfig slamConfig;
  // ---
  private Scalar lastLocalCurvature;

  SlamCurvatureObserver(SlamConfig slamConfig) {
    this.slamConfig = slamConfig;
    lastLocalCurvature = RealScalar.of(0);
  }

  public boolean curvatureContinuous(Scalar currentLocalCurvature) {
    Scalar deltaCurvatureMagn = lastLocalCurvature.abs().subtract(currentLocalCurvature.abs());
    Scalar deltaCurvature = lastLocalCurvature.subtract(currentLocalCurvature);
    // if new curvature magnitude is smaller
    if (Scalars.lessEquals(RealScalar.of(0), deltaCurvatureMagn))
      // if new curvature is within down threshold to old curvature
      if (Scalars.lessEquals(deltaCurvature.abs(), slamConfig.deltaCurvatureDownthreshold)) {
        lastLocalCurvature = currentLocalCurvature;
        return true;
      }
    // when new curvature is bigger, we have a smaller threshold
    if (Scalars.lessEquals(deltaCurvature.abs(), slamConfig.deltaCurvatureUpThreshold)) {
      lastLocalCurvature = currentLocalCurvature;
      return true;
    }
    return false;
  }
}
