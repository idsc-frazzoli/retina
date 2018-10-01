// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class TimedFuseTest extends TestCase {
  public void testSimple() throws InterruptedException {
    TimedFuse timedFuse = new TimedFuse(0.1); // 100[ms]
    assertFalse(timedFuse.isBlown());
    Thread.sleep(120);
    assertTrue(timedFuse.isBlown());
    timedFuse.pacify();
    assertFalse(timedFuse.isBlown());
    Thread.sleep(40);
    assertFalse(timedFuse.isBlown());
    Thread.sleep(100);
    assertTrue(timedFuse.isBlown());
    timedFuse.pacify();
    assertFalse(timedFuse.isBlown());
  }
}
