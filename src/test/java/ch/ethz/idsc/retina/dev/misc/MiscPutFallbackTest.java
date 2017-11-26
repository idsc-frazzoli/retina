// code by jph
package ch.ethz.idsc.retina.dev.misc;

import junit.framework.TestCase;

public class MiscPutFallbackTest extends TestCase {
  public void testRegistered() {
    try {
      MiscSocket.INSTANCE.addPutProvider(MiscPutFallback.INSTANCE);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
