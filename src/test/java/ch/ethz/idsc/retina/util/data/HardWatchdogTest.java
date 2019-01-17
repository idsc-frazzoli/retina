// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class HardWatchdogTest extends TestCase {
  public void testSimple() throws Exception {
    Watchdog watchdog = HardWatchdog.notified(0.05);
    assertFalse(watchdog.isBarking());
    Thread.sleep(20);
    assertFalse(watchdog.isBarking());
    watchdog.notifyWatchdog();
    assertFalse(watchdog.isBarking());
    Thread.sleep(20);
    assertFalse(watchdog.isBarking());
    watchdog.notifyWatchdog();
    assertFalse(watchdog.isBarking());
    Thread.sleep(70);
    assertTrue(watchdog.isBarking());
  }

  public void testLazy() throws Exception {
    Watchdog watchdog = HardWatchdog.notified(0.05);
    assertFalse(watchdog.isBarking());
    Thread.sleep(70);
    watchdog.notifyWatchdog();
    assertTrue(watchdog.isBarking());
  }
}
