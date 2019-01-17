// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class SoftWatchdogTest extends TestCase {
  public void testPacified() throws InterruptedException {
    Watchdog watchdog = SoftWatchdog.notified(0.1); // 100[ms]
    assertFalse(watchdog.isBarking());
    Thread.sleep(120);
    assertTrue(watchdog.isBarking());
    watchdog.notifyWatchdog();
    assertFalse(watchdog.isBarking());
    Thread.sleep(40);
    assertFalse(watchdog.isBarking());
    Thread.sleep(100);
    assertTrue(watchdog.isBarking());
    watchdog.notifyWatchdog();
    assertFalse(watchdog.isBarking());
  }

  public void testBlown() throws InterruptedException {
    Watchdog watchdog = SoftWatchdog.barking(0.1); // 100[ms]
    assertTrue(watchdog.isBarking());
    Thread.sleep(10);
    assertTrue(watchdog.isBarking());
    watchdog.notifyWatchdog();
    assertFalse(watchdog.isBarking());
    Thread.sleep(40);
    assertFalse(watchdog.isBarking());
    Thread.sleep(100);
    assertTrue(watchdog.isBarking());
    watchdog.notifyWatchdog();
    assertFalse(watchdog.isBarking());
  }

  public void testBarking() throws Exception {
    Watchdog watchdog = SoftWatchdog.barking(0.01);
    assertTrue(watchdog.isBarking());
    watchdog.notifyWatchdog();
    assertFalse(watchdog.isBarking());
    Thread.sleep(10);
    assertTrue(watchdog.isBarking());
  }

  public void testNotified() throws Exception {
    Watchdog watchdog = SoftWatchdog.notified(0.01);
    assertFalse(watchdog.isBarking());
    watchdog.notifyWatchdog();
    assertFalse(watchdog.isBarking());
    Thread.sleep(10);
    assertTrue(watchdog.isBarking());
  }
}
