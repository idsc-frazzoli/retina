// code by jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class TrajectoryConfigTest extends TestCase {
  public void testSimple() {
    Scalar scalar = //
        Magnitude.PER_METER.apply(TrajectoryConfig.GLOBAL.maxRotation);
    SteerConfig.GLOBAL.getAngleLimit().requireInside(Quantity.of(scalar, "rad"));
  }
}
