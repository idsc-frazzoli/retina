// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ class SlamCurvatureSmoother {
  // TODO MG
  private final Scalar alphaCurvature = SlamDvsConfig.eventCamera.slamPrcConfig.alphaCurvature;
  private final int extractionLength = SlamDvsConfig.eventCamera.slamPrcConfig.extractionPoints.number().intValue();
  // ---
  private Scalar lastLocalCurvature = RealScalar.ZERO;

  /** averages the local curvature over that last segment of the interpolated curve and then applies a 1st order IIR filter */
  public Scalar smoothCurvature(Tensor interpolatedCurve) {
    Scalar spatialAvgCurvature = interpolatedCurve.length() < extractionLength //
        ? lastLocalCurvature
        : (Scalar) Mean.of(SlamCurveUtil.localCurvature(interpolatedCurve.extract( //
            interpolatedCurve.length() - extractionLength, interpolatedCurve.length())));
    return getTemporalAvgCurvature(spatialAvgCurvature);
  }

  /** 1st order IIR filter */
  private Scalar getTemporalAvgCurvature(Scalar currentCurvature) {
    lastLocalCurvature = LinearInterpolation.of(Tensors.of(currentCurvature, lastLocalCurvature)).At(alphaCurvature);
    return lastLocalCurvature;
  }
}
