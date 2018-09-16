// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.owl.subdiv.curve.BSpline2CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.RnGeodesic;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Nest;

/** interpolates a curve */
/* package */ class SlamCurveInterpolate extends AbstractSlamCurveStep {
  private static final CurveSubdivision CURVE_SUBDIVISION = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
  // ---
  private final int iterations;

  SlamCurveInterpolate(SlamCurveContainer slamCurveContainer) {
    super(slamCurveContainer);
    iterations = Magnitude.ONE.toInt(SlamPrcConfig.GLOBAL.iterations);
  }

  @Override // from CurveListener
  public void process() {
    Tensor curve = slamCurveContainer.getSelectedPoints();
    slamCurveContainer.setInterpolation(refineFeaturePoints(curve, iterations));
  }

  /** interpolates a curve by applying BSpline2CurveSubdivision a variable number of times
   * 
   * @param featurePoints
   * @param iterations
   * @return interpolated curve */
  private Tensor refineFeaturePoints(Tensor featurePoints, int iterations) {
    return Nest.of(CURVE_SUBDIVISION::string, featurePoints, iterations);
  }
}
