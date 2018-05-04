// code by jph
package ch.ethz.idsc.retina.dev.misc;

import junit.framework.TestCase;

public class MiscPutEventTest extends TestCase {
  public void testSimple() {
    assertEquals(MiscPutEvent.FALLBACK.resetConnection, 0);
    assertEquals(MiscPutEvent.FALLBACK.length(), 6);
  }

  public void testResetcon() {
    assertEquals(MiscPutEvent.RESETCON.resetConnection, 1);
  }
}
