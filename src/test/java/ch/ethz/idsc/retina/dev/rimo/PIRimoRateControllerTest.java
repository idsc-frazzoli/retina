// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.Units;
import junit.framework.TestCase;

public class PIRimoRateControllerTest extends TestCase {
  public void testSimple() {
    PIRimoRateController c = new PIRimoRateController();
    Scalar vel_error = Quantity.of(31, RimoGetTire.RATE_UNIT); // rad*s^-1
    Scalar arms = c.iterate(vel_error);
    assertEquals(Units.of(arms), Unit.of("ARMS"));
  }
}
