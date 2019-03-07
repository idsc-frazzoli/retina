// code by jph, az
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class AntiWindupRimoRateControllerTest extends TestCase {
  public void testErrorZero() {
    System.out.println("Kp   =" + RimoConfig.GLOBAL.Kp);
    System.out.println("Ki   =" + RimoConfig.GLOBAL.Ki);
    System.out.println("Kawu =" + RimoConfig.GLOBAL.Kawu);
    AntiWindupRimoRateController srrc = new AntiWindupRimoRateController(RimoConfig.GLOBAL);
    {
      Scalar scalar = srrc.iterate(Quantity.of(10, "rad*s^-1")); // initially large error
      Magnitude.ARMS.apply(scalar);
    }
    for (int count = 0; count < 5; ++count) {
      Scalar scalar = srrc.iterate(Quantity.of(0.1, "rad*s^-1")); // small error
      System.out.println(scalar);
    }
    for (int count = 0; count < 5; ++count) {
      Scalar scalar = srrc.iterate(Quantity.of(0.0, "rad*s^-1")); // no error
      System.out.println(scalar);
    }
  }
}
