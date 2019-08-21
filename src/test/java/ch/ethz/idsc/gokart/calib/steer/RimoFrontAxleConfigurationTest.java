// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.io.IOException;

import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class RimoFrontAxleConfigurationTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    RimoFrontAxleConfiguration rimoFrontAxleConfiguration = //
        Serialization.copy(new RimoFrontAxleConfiguration(Quantity.of(0.2, "SCE")));
    WheelConfiguration wheelConfiguration = Serialization.copy(rimoFrontAxleConfiguration.wheel(0));
    Sign.requirePositive(wheelConfiguration.local().Get(2));
    rimoFrontAxleConfiguration.wheel(1);
    try {
      rimoFrontAxleConfiguration.wheel(2);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
