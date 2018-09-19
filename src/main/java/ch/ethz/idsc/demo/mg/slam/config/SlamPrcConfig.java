// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** parameters for the map processing steps of the SLAM algorithm */
public class SlamPrcConfig {
  public static final SlamPrcConfig GLOBAL = new SlamPrcConfig();
  // SlamWaypointDetection
  public Scalar mapThreshold = RealScalar.of(0.3); // valid range [0,1]
  // RegionOfInterestFilter
  public final Scalar visibleBoxHalfWidth = RealScalar.of(1.5); // [m] in go kart frame
  public final Scalar visibleBoxXMin = RealScalar.of(-3); // [m] in go kart frame
  public final Scalar visibleBoxXMax = RealScalar.of(5); // [m] in go kart frame
  // MergeWaypointFilter
  public Scalar deltaPosThreshold = RealScalar.of(0.5); // [m] in go kart frame
  // SausageFilter
  public Scalar distanceThreshold = RealScalar.of(0.4); // [m]
  public Scalar validPointsThreshold = RealScalar.of(4); // [-]
  // CurvatureFilter
  public Scalar curvatureThreshold = RealScalar.of(0.3); // [rad/m]
  // SlamCurveInterpolate
  public final Scalar iterations = RealScalar.of(2);
  // SlamCurveExtrapolate
  public Scalar curveFactor = RealScalar.of(1);
  public Scalar extrapolationDistance = Quantity.of(6, SI.METER);
  public final Scalar numberOfPoints = RealScalar.of(4).multiply(extrapolationDistance);
  // SlamCurvatureFilter
  public final Scalar alphaCurvature = RealScalar.of(0.85);
  public final Scalar extractionPoints = RealScalar.of(6);
  // SlamHeadingFilter
  public final Scalar alphaHeading = RealScalar.of(0.8);
  // SlamCurvePurePursuitModule
  public Scalar lookAhead = RealScalar.of(3.5); // [m]
}
