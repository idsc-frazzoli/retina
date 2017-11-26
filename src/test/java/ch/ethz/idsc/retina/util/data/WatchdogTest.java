// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class WatchdogTest extends TestCase {
  public void testSimple() throws Exception {
    Watchdog watchdog = new Watchdog(0.05);
    assertFalse(watchdog.isBlown());
    Thread.sleep(20);
    assertFalse(watchdog.isBlown());
    watchdog.pacify();
    assertFalse(watchdog.isBlown());
    Thread.sleep(20);
    assertFalse(watchdog.isBlown());
    watchdog.pacify();
    assertFalse(watchdog.isBlown());
    Thread.sleep(70);
    assertTrue(watchdog.isBlown());
  }

  public void testLazy() throws Exception {
    Watchdog watchdog = new Watchdog(0.05);
    assertFalse(watchdog.isBlown());
    Thread.sleep(70);
    watchdog.pacify();
    assertTrue(watchdog.isBlown());
  }
}
