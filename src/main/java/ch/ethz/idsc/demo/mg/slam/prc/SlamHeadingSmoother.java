// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class SlamHeadingSmoother {
  private final Scalar alphaHeading = SlamPrcConfig.GLOBAL.alphaHeading;
  private final Scalar betaHeading;
  // ---
  private Scalar lastEndHeading;
  private boolean initialized;

  SlamHeadingSmoother() {
    betaHeading = RealScalar.of(1).subtract(alphaHeading);
  }

  public Tensor smoothHeading(Tensor interpolatedCurve) {
    Tensor endPose = SlamCurveUtil.getEndPose(interpolatedCurve);
    Scalar heading = endPose.Get(2);
    initialize(heading);
    heading = getAvgHeading(heading);
    endPose.set(heading, 2);
    return endPose;
  }

  private void initialize(Scalar heading) {
    if (!initialized) {
      lastEndHeading = heading;
      initialized = true;
    }
  }

  /** 1st order IIR filter */
  public Scalar getAvgHeading(Scalar currentHeading) {
    Scalar avgHeading = (lastEndHeading.multiply(alphaHeading)).add(currentHeading.multiply(betaHeading));
    lastEndHeading = avgHeading;
    return avgHeading;
  }
}
