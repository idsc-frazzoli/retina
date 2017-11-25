// code by jph
package ch.ethz.idsc.retina.dev.steer;

import junit.framework.TestCase;

public class SteerPutFallbackTest extends TestCase {
  public void testRegistered() {
    try {
      SteerSocket.INSTANCE.addPutProvider(SteerPutFallback.INSTANCE);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
