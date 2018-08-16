// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SIDerivedTest extends TestCase {
  public void testSimple() {
    Scalar scalar = QuantityMagnitude.SI().in(SIDerived.RADIAN).apply(Quantity.of(360, "deg"));
    assertTrue(Chop._13.close(RealScalar.of(6.283185307179586), scalar));
  }
}
