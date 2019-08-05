// code by am
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SteerVibrationModuleTest extends TestCase {
  public void testSimple() {
    SteerVibrationModule steerVibrationModule = new SteerVibrationModule();
    steerVibrationModule.first();
    assertFalse(steerVibrationModule.putEvent().isPresent());
    steerVibrationModule.last();
  }

  public void testTime2Torque() {
    SteerVibrationModule steerVibrationModule = new SteerVibrationModule();
    Scalar scalar = steerVibrationModule.time2torque(Quantity.of(0.3, SI.SECOND));
    SteerPutEvent.RTORQUE.apply(scalar);
  }
}
