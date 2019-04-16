// code by jph, az
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
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
      Scalar scalar = srrc.iterate(Quantity.of(10, SI.PER_SECOND)); // initially large error
      Magnitude.ARMS.apply(scalar);
    }
    for (int count = 0; count < 5; ++count) {
      Scalar scalar = srrc.iterate(Quantity.of(0.1, SI.PER_SECOND)); // small error
      System.out.println(scalar);
    }
    for (int count = 0; count < 5; ++count) {
      Scalar scalar = srrc.iterate(Quantity.of(0.0, SI.PER_SECOND)); // no error
      System.out.println(scalar);
    }
  }
}
