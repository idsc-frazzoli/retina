// code by jph
package ch.ethz.idsc.owl.car.slip;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class AngularSlipTest extends TestCase {
  public void testSimple() {
    AngularSlip angularSlip = new AngularSlip( //
        Quantity.of(2, "m*s^-1"), //
        Quantity.of(0.37, "m^-1"), //
        Quantity.of(0.4, "s^-1"));
    Chop._10.requireClose(angularSlip.angularSlip(), Tensors.fromString("0.34[s^-1]"));
    Chop._10.requireClose(angularSlip.wantedRotationRate(), Quantity.of(0.74, "s^-1")); // 0.74[s^-1]
  }
}
