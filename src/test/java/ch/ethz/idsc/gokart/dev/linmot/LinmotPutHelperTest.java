// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class LinmotPutHelperTest extends TestCase {
  public void testSimple() {
    assertTrue(LinmotPutOperation.INSTANCE.fallback().isOperational());
  }

  public void testDoublePos() {
    LinmotPutEvent lpe1 = LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.ZERO);
    assertEquals(lpe1.target_position, -50);
    LinmotPutEvent lpe2 = LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.ONE);
    assertEquals(lpe2.target_position, -500);
    LinmotPutEvent lpe3 = LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.of(.5));
    assertEquals(lpe3.target_position, -275);
  }
}
