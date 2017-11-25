// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import junit.framework.TestCase;

public class RimoSocketTest extends TestCase {
  public void testRate() {
    assertEquals(RimoSocket.SEND_PERIOD_MS, 20);
  }
}
