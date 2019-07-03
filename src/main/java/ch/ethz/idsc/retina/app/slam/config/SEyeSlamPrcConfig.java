// code by mg
package ch.ethz.idsc.retina.app.slam.config;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** sets SlamPrcConfig parameters according to siliconEye */
/* package */ class SEyeSlamPrcConfig extends SlamPrcConfig {
  public static final SEyeSlamPrcConfig GLOBAL = new SEyeSlamPrcConfig();

  public SEyeSlamPrcConfig() {
    // SlamWaypointDetection
    /** valid range [0, 1] */
    mapThreshold = RealScalar.of(0.4);
    // RegionOfInterestFilter
    visibleBoxXMin = Quantity.of(-3, SI.METER); // [m] in go kart frame
    visibleBoxXMax = Quantity.of(8, SI.METER); // [m] in go kart frame
    /** half 'width' of rectangle for RegionOfInterestFilter */
    visibleBoxYHalfWidth = Quantity.of(1.5, SI.METER); // [m] in go kart frame
    // MergeWaypointFilter
    deltaPosThreshold = RealScalar.of(0.6); // [m] in go kart frame
    // SausageFilter
    distanceThreshold = RealScalar.of(0.3); // [m]
    validPointsThreshold = RealScalar.of(4); // []
    // CurvatureFilter
    curvatureThreshold = RealScalar.of(0.3); // [rad/m]
    // SlamCurveInterpolate
    iterations = RealScalar.of(2);
    // SlamCurveExtrapolate
    curveFactor = RealScalar.of(1);
    extrapolationDistance = Quantity.of(6, SI.METER);
    numberOfPoints = RealScalar.of(4).multiply(extrapolationDistance);
    // SlamCurvatureSmoother
    /** alphaCurvature is the weight for the last curvature in the filter
     * alphaCurvature is required to be in the interval [0, 1] */
    alphaCurvature = RealScalar.of(0.92); // []
    /** mimimum number of curve points to average curvature from
     * see SlamCurvatureSmoother */
    extractionPoints = RealScalar.of(6); // []
    // SlamHeadingFilter
    alphaHeading = RealScalar.of(0.85); // []
    // SlamCurvePurePursuitModule
    lookAhead = Quantity.of(3.5, SI.METER);
  }
}
