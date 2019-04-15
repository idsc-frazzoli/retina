// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.util.List;

import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class RimoWheelConfigurationsTest extends TestCase {
  public void testSimple() {
    List<WheelConfiguration> list = RimoWheelConfigurations.fromSCE(Quantity.of(-0.1, "SCE"));
    assertEquals(list.size(), 4);
    Sign.requirePositive(list.get(0).local().Get(0));
    Sign.requirePositive(list.get(1).local().Get(0));
    Sign.requirePositive(list.get(0).local().Get(1));
    Sign.requirePositive(list.get(1).local().Get(1).negate());
    assertTrue(Scalars.isZero(list.get(2).local().Get(0)));
    assertTrue(Scalars.isZero(list.get(3).local().Get(0)));
    Sign.requirePositive(list.get(2).local().Get(1));
    Sign.requirePositive(list.get(3).local().Get(1).negate());
  }
}
