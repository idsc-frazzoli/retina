// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class WatchdogTest extends TestCase {
  public void testSimple() throws Exception {
    Watchdog watchdog = new Watchdog(0.05);
    assertFalse(watchdog.isWatchdogBarking());
    Thread.sleep(20);
    assertFalse(watchdog.isWatchdogBarking());
    watchdog.notifyWatchdog();
    assertFalse(watchdog.isWatchdogBarking());
    Thread.sleep(20);
    assertFalse(watchdog.isWatchdogBarking());
    watchdog.notifyWatchdog();
    assertFalse(watchdog.isWatchdogBarking());
    Thread.sleep(70);
    assertTrue(watchdog.isWatchdogBarking());
  }

  public void testLazy() throws Exception {
    Watchdog watchdog = new Watchdog(0.05);
    assertFalse(watchdog.isWatchdogBarking());
    Thread.sleep(70);
    watchdog.notifyWatchdog();
    assertTrue(watchdog.isWatchdogBarking());
  }
}
