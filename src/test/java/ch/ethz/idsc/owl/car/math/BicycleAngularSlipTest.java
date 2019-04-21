// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class BicycleAngularSlipTest extends TestCase {
  public void testSimple() {
    AngularSlip scalar = new BicycleAngularSlip(Quantity.of(2, SI.METER)).getAngularSlip( //
        Quantity.of(0.37, ""), //
        Quantity.of(2, SI.VELOCITY), //
        Quantity.of(0.4, SI.PER_SECOND));
    Chop._10.requireClose(scalar.angularSlip(), Tensors.fromString("-0.012136838344150969[s^-1]"));
  }

  public void testFail() {
    try {
      new BicycleAngularSlip(RealScalar.ZERO);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
