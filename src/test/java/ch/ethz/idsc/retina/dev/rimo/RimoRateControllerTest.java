// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.Units;
import junit.framework.TestCase;

public class RimoRateControllerTest extends TestCase {
  public void testSimple() {
    RimoRateController rimoRateController = new RimoRateController();
    Scalar vel_error = Quantity.of(31, RimoGetTire.UNIT_RATE); // rad*s^-1
    Scalar arms = rimoRateController.iterate(vel_error);
    assertEquals(Units.of(arms), Unit.of("ARMS"));
  }

  public void testDt() {
    assertEquals(RimoRateController.DT, Quantity.of(0.02, "s"));
  }
}
