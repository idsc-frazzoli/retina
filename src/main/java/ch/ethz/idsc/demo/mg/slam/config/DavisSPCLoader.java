// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** sets SlamPrcConfig parameters according to davis */
/* package */ enum DavisSPCLoader {
  ;
  public static SlamPrcConfig getSlamPrcConfig() {
    SlamPrcConfig slamPrcConfig = new SlamPrcConfig();
    // SlamWaypointDetection
    /** valid range [0,1] */
    slamPrcConfig.mapThreshold = RealScalar.of(0.4);
    // RegionOfInterestFilter
    slamPrcConfig.visibleBoxXMin = Quantity.of(-3, SI.METER); // [m] in go kart frame
    slamPrcConfig.visibleBoxXMax = Quantity.of(5, SI.METER); // [m] in go kart frame
    /** half 'width' of rectangle for RegionOfInterestFilter */
    slamPrcConfig.visibleBoxYHalfWidth = Quantity.of(1, SI.METER); // [m] in go kart frame
    // MergeWaypointFilter
    slamPrcConfig.deltaPosThreshold = RealScalar.of(0.6); // [m] in go kart frame
    // SausageFilter
    slamPrcConfig.distanceThreshold = RealScalar.of(0.3); // [m]
    slamPrcConfig.validPointsThreshold = RealScalar.of(4); // [-]
    // CurvatureFilter
    slamPrcConfig.curvatureThreshold = RealScalar.of(0.3); // [rad/m]
    // SlamCurveInterpolate
    slamPrcConfig.iterations = RealScalar.of(2);
    // SlamCurveExtrapolate
    slamPrcConfig.curveFactor = RealScalar.of(1);
    slamPrcConfig.extrapolationDistance = Quantity.of(6, SI.METER);
    slamPrcConfig.numberOfPoints = RealScalar.of(4).multiply(slamPrcConfig.extrapolationDistance);
    // SlamCurvatureSmoother
    /** alphaCurvature is the weight for the last curvature in the filter
     * alphaCurvature is required to be in the interval [0, 1] */
    slamPrcConfig.alphaCurvature = RealScalar.of(0.92); // [-]
    /** mimimum number of curve points to average curvature from
     * see SlamCurvatureSmoother */
    slamPrcConfig.extractionPoints = RealScalar.of(6); // [-]
    // SlamHeadingFilter
    slamPrcConfig.alphaHeading = RealScalar.of(0.85); // [-]
    // SlamCurvePurePursuitModule
    slamPrcConfig.lookAhead = Quantity.of(3.5, SI.METER);
    return slamPrcConfig;
  }
}
