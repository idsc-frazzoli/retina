// code by jph
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.gokart.core.tvec.TorqueVectoringConfig;
import ch.ethz.idsc.owl.car.slip.AngularSlip;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SimpleMotorCurrentsTest extends TestCase {
  public void testSimple() {
    SimpleMotorCurrents simpleMotorCurrents = new SimpleMotorCurrents(TorqueVectoringConfig.GLOBAL);
    AngularSlip angularSlip = new AngularSlip( //
        Quantity.of(1, SI.VELOCITY), Quantity.of(0.2, SI.PER_METER), Quantity.of(0.1, SI.PER_SECOND));
    Tensor acceleration = simpleMotorCurrents.fromAcceleration(angularSlip, Quantity.of(0.5, SI.ACCELERATION));
    assertTrue(Scalars.lessThan(acceleration.Get(0), acceleration.Get(1)));
  }
}
