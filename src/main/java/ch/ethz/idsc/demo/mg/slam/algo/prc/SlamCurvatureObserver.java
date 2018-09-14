// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

// observes the local curvature and since this value should be continuous, we can disregard estimated curves with changing curvature
/* package */ class SlamCurvatureObserver {
  private final SlamConfig slamConfig;
  private final Scalar alphaCurvature = RealScalar.of(0.9);
  private final Scalar betaCurvature = RealScalar.of(1).subtract(alphaCurvature);
  private final Scalar alphaHeading = RealScalar.of(0.98);
  private final Scalar betaHeading = RealScalar.of(1).subtract(alphaHeading);
  // ---
  private Scalar lastLocalCurvature;
  private Scalar lastEndHeading;
  private boolean initialized;

  SlamCurvatureObserver(SlamConfig slamConfig) {
    this.slamConfig = slamConfig;
    lastLocalCurvature = RealScalar.of(0);
  }

  public void initialize(Scalar endHeading) {
    if (!initialized) {
      lastEndHeading = endHeading;
      initialized = true;
    }
  }

  public Scalar getAvgCurvature(Scalar currentCurvature) {
    Scalar avgCurvature = lastLocalCurvature.multiply(alphaCurvature).add(currentCurvature.multiply(betaCurvature));
    lastLocalCurvature = avgCurvature;
    return avgCurvature;
  }

  public Scalar getAvgHeading(Scalar currentHeading) {
    Scalar avgHeading = (lastEndHeading.multiply(alphaHeading)).add(currentHeading.multiply(betaHeading));
    lastEndHeading = avgHeading;
    return avgHeading;
  }
}
