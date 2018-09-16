// code by mg
package ch.ethz.idsc.demo.mg.slam.prc;

import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.owl.subdiv.curve.BSpline2CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.RnGeodesic;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Nest;

/** interpolates a curve through a set of points */
/* package */ class SlamCurveInterpolate extends AbstractSlamCurveStep {
  private static final CurveSubdivision CURVE_SUBDIVISION = new BSpline2CurveSubdivision(RnGeodesic.INSTANCE);
  // ---
  private final int iterations;

  SlamCurveInterpolate(SlamPrcContainer slamCurveContainer) {
    super(slamCurveContainer);
    iterations = Magnitude.ONE.toInt(SlamPrcConfig.GLOBAL.iterations);
  }

  @Override // from CurveListener
  public void process() {
    Tensor validGokartWaypoints = slamPrcContainer.getValidGokartWaypoints();
    slamPrcContainer.setInterpolatedCurve(interpolate(validGokartWaypoints, iterations));
  }

  /** interpolates a curve by applying BSpline2CurveSubdivision a variable number of times
   * 
   * @param validGokartWaypoints
   * @param iterations
   * @return interpolated curve */
  private Tensor interpolate(Tensor validGokartWaypoints, int iterations) {
    return Nest.of(CURVE_SUBDIVISION::string, validGokartWaypoints, iterations);
  }
}
