// code by jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
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
}
