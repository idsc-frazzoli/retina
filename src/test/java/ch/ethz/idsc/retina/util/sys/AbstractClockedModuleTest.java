// code by jph
package ch.ethz.idsc.retina.util.sys;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class AbstractClockedModuleTest extends TestCase {
  public void testSimple() {
    Scalar value = Magnitude.MILLI_SECOND.apply(Quantity.of(1, "s"));
    assertEquals(value, RealScalar.of(1000));
    assertEquals(value.number().longValue(), 1000);
  }

  public void testMillis() {
    Scalar value = Magnitude.MILLI_SECOND.apply(Quantity.of(20, "ms"));
    assertEquals(value, RealScalar.of(20));
    assertEquals(value.number().longValue(), 20);
  }

  public void testHertz() {
    Scalar value = Magnitude.MILLI_SECOND.apply(Quantity.of(50, "Hz").reciprocal());
    assertEquals(value, RealScalar.of(20));
    assertEquals(value.number().longValue(), 20);
  }
}
