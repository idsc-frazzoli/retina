// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import junit.framework.TestCase;

public class RimoSocketTest extends TestCase {
  public void testRate() {
    assertEquals(RimoSocket.INSTANCE.getPeriod_ms(), 20);
  }

  public void testSize() {
    assertEquals(RimoSocket.INSTANCE.getPutProviderSize(), 1);
    assertEquals(RimoSocket.INSTANCE.getGetListenersSize(), 0);
    assertEquals(RimoSocket.INSTANCE.getPutListenersSize(), 0);
  }
}
