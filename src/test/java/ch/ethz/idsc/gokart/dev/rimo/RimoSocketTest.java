// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class RimoSocketTest extends TestCase {
  public void testRate() {
    assertEquals(RimoSocket.INSTANCE.getPutPeriod_ms(), 20);
  }

  public void testSize() {
    assertTrue(1 <= RimoSocket.INSTANCE.getPutProviderSize());
    // FIXME JPH
    // assertEquals(RimoSocket.INSTANCE.getGetListenersSize(), 1);
    assertEquals(RimoSocket.INSTANCE.getPutListenersSize(), 0);
  }

  public void testGetPeriod() {
    Scalar scalar = RimoSocket.getGetPeriod();
    assertTrue(ExactScalarQ.of(scalar));
    assertEquals(scalar, Quantity.of(0.004, "s"));
  }
}
