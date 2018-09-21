// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ class SlamCurvatureSmoother {
  private final Scalar alphaCurvature = SlamPrcConfig.GLOBAL.alphaCurvature;
  private final Scalar betaCurvature;
  private final int extractionLength = SlamPrcConfig.GLOBAL.extractionPoints.number().intValue();
  // ---
  private Scalar lastLocalCurvature = RealScalar.of(0);

  SlamCurvatureSmoother() {
    betaCurvature = RealScalar.of(1).subtract(alphaCurvature);
  }

  /** averages the local curvature over that last segment of the interpolated curve and then applies a 1st order IIR filter */
  public Scalar smoothCurvature(Tensor interpolatedCurve) {
    Scalar spatialAvgCurvature;
    if (interpolatedCurve.length() < extractionLength)
      spatialAvgCurvature = lastLocalCurvature;
    else
      spatialAvgCurvature = (Scalar) Mean.of(SlamCurveUtil.localCurvature(interpolatedCurve.extract( //
          interpolatedCurve.length() - extractionLength, interpolatedCurve.length())));
    Scalar temporalAvgCurvature = getTemporalAvgCurvature(spatialAvgCurvature);
    return temporalAvgCurvature;
  }

  /** 1st order IIR filter */
  private Scalar getTemporalAvgCurvature(Scalar currentCurvature) {
    Scalar avgCurvature = lastLocalCurvature.multiply(alphaCurvature).add(currentCurvature.multiply(betaCurvature));
    lastLocalCurvature = avgCurvature;
    return avgCurvature;
  }
}
