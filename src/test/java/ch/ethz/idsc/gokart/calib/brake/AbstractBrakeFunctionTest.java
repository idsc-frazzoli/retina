// code by jph
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class AbstractBrakeFunctionTest extends TestCase {
  public void testSimple() {
    assertEquals(AbstractBrakeFunction.decelerationFromPos(Quantity.of(-1, SI.METER)), Quantity.of(0, SI.ACCELERATION));
    assertEquals(AbstractBrakeFunction.decelerationFromPos(Quantity.of(0, SI.METER)), Quantity.of(0, SI.ACCELERATION));
    assertEquals(AbstractBrakeFunction.decelerationFromPos(Quantity.of(0.02, SI.METER)), Quantity.of(0, SI.ACCELERATION));
    assertEquals(AbstractBrakeFunction.decelerationFromPos(Quantity.of(0.025, SI.METER)), Quantity.of(0, SI.ACCELERATION));
    Chop._12.requireClose(AbstractBrakeFunction.decelerationFromPos(Quantity.of(0.03, SI.METER)), Quantity.of(1.27755, SI.ACCELERATION));
    Chop._12.requireClose(AbstractBrakeFunction.decelerationFromPos(Quantity.of(0.04, SI.METER)), Quantity.of(3.83145, SI.ACCELERATION));
    // TODO MH cap values otherwise deceleration is implausible
    Chop._12.requireClose(AbstractBrakeFunction.decelerationFromPos(Quantity.of(0.05, SI.METER)), Quantity.of(6.38375, SI.ACCELERATION));
    Chop._12.requireClose(AbstractBrakeFunction.decelerationFromPos(Quantity.of(0.1, SI.METER)), Quantity.of(19.12125, SI.ACCELERATION));
  }

  public void testGetDeceleration() {
    Scalar scalar = AbstractBrakeFunction.getDeceleration(Quantity.of(0.03, SI.METER), RealScalar.of(0.7828));
    Chop._03.requireClose(scalar, Quantity.of(1, SI.ACCELERATION));
  }
}
