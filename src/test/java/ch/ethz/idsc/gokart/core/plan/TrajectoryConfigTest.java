// code by jph
package ch.ethz.idsc.gokart.core.plan;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class TrajectoryConfigTest extends TestCase {
  public void testSimple() {
    Scalar scalar = UnitSystem.SI().apply(TrajectoryConfig.GLOBAL.maxRotation);
    SteerConfig.GLOBAL.getRatioLimit().requireInside(scalar);
  }

  public void testCutoff() {
    Scalar scalar = TrajectoryConfig.GLOBAL.getCutoffDistance(Quantity.of(3, "m*s^-1"));
    Magnitude.METER.apply(scalar);
    Sign.requirePositive(scalar);
  }

  public void testCutoffNonNegative() {
    Scalar scalar = TrajectoryConfig.GLOBAL.getCutoffDistance(Quantity.of(-5.3, "m*s^-1"));
    Magnitude.METER.apply(scalar);
    Sign.requirePositive(scalar);
  }

  public void testControlResolution() {
    assertTrue(IntegerQ.of(TrajectoryConfig.GLOBAL.controlResolution));
  }

  public void testConeHalfAngle() {
    assertEquals(RealScalar.of(Math.PI / 10), Degree.of(18));
  }

  public void testExpandFraction() {
    Clips.unit().requireInside(TrajectoryConfig.GLOBAL.expandFraction);
  }

  public void testTimeLimit() {
    Scalar timeLimit = TrajectoryConfig.GLOBAL.expandTimeLimit();
    Scalar seconds = Magnitude.SECOND.apply(timeLimit);
    Clips.unit().requireInside(seconds);
  }

  public void testResample1() {
    Tensor curveNonUnits = ResourceData.of("/dubilab/waypoints/20190507.csv");
    Tensor curve = Tensor.of(curveNonUnits.stream().map(PoseHelper::attachUnits));
    Tensor waypoints = TrajectoryConfig.GLOBAL.resampledWaypoints(curve);
    MatrixQ.require(waypoints);
    PoseHelper.toUnitless(waypoints.get(0));
  }

  public void testResample2() {
    Tensor curveNonUnits = ResourceData.of("/dubilab/waypoints/20190325.csv");
    Tensor curve = Tensor.of(curveNonUnits.stream().map(PoseHelper::attachUnits));
    Tensor waypoints = TrajectoryConfig.GLOBAL.resampledWaypoints(curve, false);
    MatrixQ.require(waypoints);
    PoseHelper.toUnitless(waypoints.get(0));
  }
}
