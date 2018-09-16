// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

// contains parameters for map processing steps of SLAM algorithm
public class SlamPrcConfig {
  public static final SlamPrcConfig GLOBAL = new SlamPrcConfig();
  // SlamWaypointSelection
  public final Scalar visibleBoxHalfWidth = Quantity.of(1.5, SI.METER); // in go kart frame
  public final Scalar visibleBoxXMin = Quantity.of(-3, SI.METER); // in go kart frame
  public final Scalar visibleBoxXMax = Quantity.of(5, SI.METER); // in go kart frame
  // SlamCurveInterpolate
  public final Scalar iterations = RealScalar.of(2);
  // SlamWaypointFilter
  public final Scalar deltaYDistance = Quantity.of(0.3, SI.METER);
  public final Scalar deltaPos = Quantity.of(0.4, SI.METER);
  // SlamCurveExtrapolate
  public Scalar curveFactor = RealScalar.of(1.5);
  public Scalar extrapolationDistance = Quantity.of(6, SI.METER);
  public final Scalar numberOfPoints = RealScalar.of(4).multiply(extrapolationDistance);
  // SlamCurvePurePursuitModule
  public Scalar lookAhead = RealScalar.of(3.5); // [m]
}
