// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class NonSITest extends TestCase {
  public void testSimple() {
    Scalar scalar = Magnitude.MILLI_SECOND.apply(Quantity.of(1000, NonSI.MICRO_SECOND));
    assertTrue(ExactScalarQ.of(scalar));
    assertEquals(scalar, RealScalar.ONE);
  }
}
