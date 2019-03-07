// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.List;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.IntegerQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class TrajectoryConfigTest extends TestCase {
  public void testSimple() {
    Scalar scalar = //
        Magnitude.PER_METER.apply(TrajectoryConfig.GLOBAL.maxRotation);
    SteerConfig.GLOBAL.getAngleLimit().requireInside(Quantity.of(scalar, "rad"));
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

  public void testWaypoints() {
    Tensor tensor = TrajectoryConfig.getWaypoints();
    List<Integer> dims = Dimensions.of(tensor);
    assertTrue(1 < dims.get(0));
    assertEquals((int) dims.get(1), 3); // {x, y, theta}
  }

  public void testExpandFraction() {
    Clip.unit().requireInside(TrajectoryConfig.GLOBAL.expandFraction);
  }

  public void testTimeLimit() {
    Scalar timeLimit = TrajectoryConfig.GLOBAL.expandTimeLimit();
    Scalar seconds = Magnitude.SECOND.apply(timeLimit);
    Clip.unit().requireInside(seconds);
  }
}
