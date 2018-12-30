// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SIDerived;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.qty.Unit;
import junit.framework.TestCase;

public class LookupRimoRateControllerTest extends TestCase {
  public void testSimple() {
    RimoRateController rimoRateController = new LookupRimoRateController(RimoConfig.GLOBAL);
    Scalar vel_error = Quantity.of(31, SIDerived.RADIAN_PER_SECOND); // rad*s^-1
    rimoRateController.setWheelRate(Quantity.of(2, "rad*s^-1"));
    Scalar arms = rimoRateController.iterate(vel_error);
    assertEquals(QuantityUnit.of(arms), Unit.of("ARMS"));
  }

  public void testDt() {
    assertEquals(LookupRimoRateController.DT, Quantity.of(0.02, "s"));
  }

  public void testErrorZero() {
    System.out.println("Kp   =" + RimoConfig.GLOBAL.Kp);
    System.out.println("Ki   =" + RimoConfig.GLOBAL.Ki);
    System.out.println("AWP =" + RimoConfig.GLOBAL.lAntiWindupPadding);
    LookupRimoRateController srrc = new LookupRimoRateController(RimoConfig.GLOBAL);
    {
      Scalar scalar = srrc.iterate(Quantity.of(10, "rad*s^-1")); // initially large error
      Magnitude.ARMS.apply(scalar);
      assertTrue(Scalars.lessEquals(Quantity.of(0, NonSI.ARMS), scalar));
    }
    for (int count = 0; count < 1000; ++count) {
      @SuppressWarnings("unused")
      Scalar scalar = srrc.iterate(Quantity.of(0.1, "rad*s^-1")); // check integral part
      // System.out.println(scalar);
    }
  }
}
