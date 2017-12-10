// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class LinmotPutHelperTest extends TestCase {
  public void testSimple() {
    assertTrue(LinmotPutHelper.FALLBACK_OPERATION.isOperational());
  }

  public void testDoublePos() {
    LinmotPutEvent lpe1 = LinmotPutHelper.operationToRelativePosition(RealScalar.ZERO);
    assertEquals(lpe1.target_position, -50);
    LinmotPutEvent lpe2 = LinmotPutHelper.operationToRelativePosition(RealScalar.ONE);
    assertEquals(lpe2.target_position, -500);
    LinmotPutEvent lpe3 = LinmotPutHelper.operationToRelativePosition(RealScalar.of(.5));
    assertEquals(lpe3.target_position, -275);
  }
}
