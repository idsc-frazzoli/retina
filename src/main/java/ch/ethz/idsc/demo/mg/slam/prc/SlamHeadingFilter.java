// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class SlamHeadingFilter {
  private final Scalar alphaHeading = SlamPrcConfig.GLOBAL.alphaHeading;
  private final Scalar betaHeading = SlamPrcConfig.GLOBAL.beataHeading;
  // ---
  private Scalar lastEndHeading = RealScalar.of(0);
  private boolean initialized;

  public Tensor filterHeading(Tensor interpolatedCurve) {
    Tensor endPose = SlamCurveUtil.getEndPose(interpolatedCurve);
    Scalar heading = endPose.Get(2);
    initialize(heading);
    heading = lastEndHeading;
    heading = getAvgHeading(heading);
    endPose.set(heading, 2);
    return endPose;
  }

  private void initialize(Scalar endHeading) {
    if (!initialized) {
      lastEndHeading = endHeading;
      initialized = true;
    }
  }

  public Scalar getAvgHeading(Scalar currentHeading) {
    Scalar avgHeading = (lastEndHeading.multiply(alphaHeading)).add(currentHeading.multiply(betaHeading));
    lastEndHeading = avgHeading;
    return avgHeading;
  }
}
