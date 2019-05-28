// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class AngularSlipTest extends TestCase {
  public void testSimple() {
    AngularSlip angularSlip = new AngularSlip( //
        Quantity.of(2, SI.VELOCITY), //
        Quantity.of(0.37, SI.PER_METER), //
        Quantity.of(0.4, SI.PER_SECOND));
    Chop._10.requireClose(angularSlip.angularSlip(), Tensors.fromString("0.34[s^-1]"));
    Chop._10.requireClose(angularSlip.wantedRotationRate(), Quantity.of(0.74, SI.PER_SECOND)); // 0.74[s^-1]
  }
}
