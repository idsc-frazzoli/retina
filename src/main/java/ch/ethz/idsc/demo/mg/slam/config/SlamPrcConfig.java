// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** parameters for the map processing steps of the SLAM algorithm */
public class SlamPrcConfig {
  public static final SlamPrcConfig GLOBAL = new SlamPrcConfig();
  // SlamWaypointDetection
  /** valid range [0,1] */
  public Scalar mapThreshold = RealScalar.of(0.25);
  // RegionOfInterestFilter
  public final Scalar visibleBoxXMin = Quantity.of(-3, SI.METER); // [m] in go kart frame
  public final Scalar visibleBoxXMax = Quantity.of(5, SI.METER); // [m] in go kart frame
  /** half 'width' of rectangle for RegionOfInterestFilter */
  public final Scalar visibleBoxYHalfWidth = Quantity.of(0.75, SI.METER); // [m] in go kart frame
  // MergeWaypointFilter
  public Scalar deltaPosThreshold = RealScalar.of(0.6); // [m] in go kart frame
  // SausageFilter
  public Scalar distanceThreshold = RealScalar.of(0.3); // [m]
  public Scalar validPointsThreshold = RealScalar.of(4); // [-]
  // CurvatureFilter
  public Scalar curvatureThreshold = RealScalar.of(0.3); // [rad/m]
  // SlamCurveInterpolate
  public final Scalar iterations = RealScalar.of(2);
  // SlamCurveExtrapolate
  public Scalar curveFactor = RealScalar.of(1);
  public Scalar extrapolationDistance = Quantity.of(6, SI.METER);
  public final Scalar numberOfPoints = RealScalar.of(4).multiply(extrapolationDistance);
  // SlamCurvatureSmoother
  /** alphaCurvature is the weight for the last curvature in the filter
   * alphaCurvature is required to be in the interval [0, 1] */
  public final Scalar alphaCurvature = RealScalar.of(0.92); // [-]
  /** mimimum number of curve points to average curvature from
   * see SlamCurvatureSmoother */
  public final Scalar extractionPoints = RealScalar.of(6); // [-]
  // SlamHeadingFilter
  public final Scalar alphaHeading = RealScalar.of(0.85); // [-]
  // SlamCurvePurePursuitModule
  public Scalar lookAhead = Quantity.of(3.5, SI.METER);

  /***************************************************/
  /** @return unitless look ahead distance with interpretation in meters */
  public Scalar lookAheadMeter() {
    return Magnitude.METER.apply(lookAhead);
  }
}
