// code by jph
package ch.ethz.idsc.retina.util.math;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class NonSITest extends TestCase {
  public void testTemporal() {
    Scalar scalar = Magnitude.MILLI_SECOND.apply(Quantity.of(1000, NonSI.MICRO_SECOND));
    assertTrue(ExactScalarQ.of(scalar));
    assertEquals(scalar, RealScalar.ONE);
  }

  public void testDegree() {
    Scalar scalar = QuantityMagnitude.SI().in(SI.ONE).apply(Quantity.of(360, "deg"));
    assertTrue(Chop._13.close(RealScalar.of(6.283185307179586), scalar));
  }
}
