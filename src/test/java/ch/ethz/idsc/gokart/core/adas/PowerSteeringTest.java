// code by am
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PowerSteeringTest extends TestCase {
  public void testNonNull() {
    PowerSteering powerSteeringModule = new NaivePowerSteering(HapticSteerConfig.GLOBAL);
    Scalar scalar = powerSteeringModule.torque(Quantity.of(0.2, "SCE"), Tensors.of( //
        Quantity.of(2, SI.VELOCITY), //
        Quantity.of(0.3, SI.VELOCITY), //
        Quantity.of(1, SI.PER_SECOND)), //
        Quantity.of(0.3, "SCT"));
    assertTrue(Scalars.nonZero(SteerPutEvent.RTORQUE.apply(scalar)));
  }

  public void testNullFail() {
    try {
      new NaivePowerSteering(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
