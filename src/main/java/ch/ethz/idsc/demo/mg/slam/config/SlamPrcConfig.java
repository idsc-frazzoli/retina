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
  // YPosDiffFilter
  public Scalar deltaYThreshold = RealScalar.of(1); // [m] in go kart frame
  // AbsPosDiffFilter
  public Scalar deltaPosThreshold = RealScalar.of(0.6); // [m] in go kart frame
  // SlamCurveInterpolate
  public final Scalar iterations = RealScalar.of(2);
  // SlamCurveExtrapolate
  public Scalar curveFactor = RealScalar.of(1);
  public Scalar extrapolationDistance = Quantity.of(6, SI.METER);
  public final Scalar numberOfPoints = RealScalar.of(4).multiply(extrapolationDistance);
  // SlamCurvatureFilter
  public final Scalar alphaCurvature = RealScalar.of(0.9);
  public final Scalar betaCurvature = RealScalar.of(1).subtract(alphaCurvature);
  public final Scalar extractionLength = RealScalar.of(6);
  // SlamHeadingFilter
  public final Scalar alphaHeading = RealScalar.of(0.9);
  public final Scalar beataHeading = RealScalar.of(1).subtract(alphaHeading);
  // SlamCurvePurePursuitModule
  public Scalar lookAhead = RealScalar.of(3.5); // [m]
}
