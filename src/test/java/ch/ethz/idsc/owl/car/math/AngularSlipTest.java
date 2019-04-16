// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class AngularSlipTest extends TestCase {
  public void testSimple() {
    Scalar scalar = AngularSlip.of( //
        Quantity.of(0.37, ""), //
        Quantity.of(2, SI.METER), //
        Quantity.of(0.4, SI.PER_SECOND), //
        Quantity.of(2, SI.VELOCITY));
    Chop._10.requireClose(scalar, Tensors.fromString("-0.012136838344150969[s^-1]"));
  }
}
