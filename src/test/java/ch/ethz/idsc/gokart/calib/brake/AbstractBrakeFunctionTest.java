// code by jph
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class AbstractBrakeFunctionTest extends TestCase {
  public void testSimple() {
    assertEquals(AbstractBrakeFunction.getDeceleration(Quantity.of(-1, SI.METER), RealScalar.ONE), Quantity.of(0, SI.ACCELERATION));
    assertEquals(AbstractBrakeFunction.getDeceleration(Quantity.of(0, SI.METER), RealScalar.ONE), Quantity.of(0, SI.ACCELERATION));
    assertEquals(AbstractBrakeFunction.getDeceleration(Quantity.of(0.02, SI.METER), RealScalar.ONE), Quantity.of(0, SI.ACCELERATION));
    assertEquals(AbstractBrakeFunction.getDeceleration(Quantity.of(0.025, SI.METER), RealScalar.ONE), Quantity.of(0, SI.ACCELERATION));
    Chop._12.requireClose(AbstractBrakeFunction.getDeceleration(Quantity.of(0.03, SI.METER), RealScalar.ONE), Quantity.of(1.27755, SI.ACCELERATION));
    Chop._12.requireClose(AbstractBrakeFunction.getDeceleration(Quantity.of(0.04, SI.METER), RealScalar.ONE), Quantity.of(3.83145, SI.ACCELERATION));
    // TODO MH cap values otherwise deceleration is implausible
    Chop._12.requireClose(AbstractBrakeFunction.getDeceleration(Quantity.of(0.05, SI.METER), RealScalar.ONE), Quantity.of(6.38375, SI.ACCELERATION));
    Chop._12.requireClose(AbstractBrakeFunction.getDeceleration(Quantity.of(0.1, SI.METER), RealScalar.ONE), Quantity.of(19.12125, SI.ACCELERATION));
  }
}
