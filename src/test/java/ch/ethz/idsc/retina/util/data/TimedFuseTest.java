// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class TimedFuseTest extends TestCase {
  public void testSimple() throws Exception {
    TimedFuse timedFuse = new TimedFuse(0.1);
    assertFalse(timedFuse.isBlown());
    timedFuse.register(true);
    Thread.sleep(120);
    assertTrue(timedFuse.isBlown());
  }
}
