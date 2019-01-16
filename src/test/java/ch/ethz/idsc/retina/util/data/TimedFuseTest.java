// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class TimedFuseTest extends TestCase {
  public void testPacified() throws InterruptedException {
    WatchdogInterface timedFuse = TimedFuse.notified(0.1); // 100[ms]
    assertFalse(timedFuse.isWatchdogBarking());
    Thread.sleep(120);
    assertTrue(timedFuse.isWatchdogBarking());
    timedFuse.notifyWatchdog();
    assertFalse(timedFuse.isWatchdogBarking());
    Thread.sleep(40);
    assertFalse(timedFuse.isWatchdogBarking());
    Thread.sleep(100);
    assertTrue(timedFuse.isWatchdogBarking());
    timedFuse.notifyWatchdog();
    assertFalse(timedFuse.isWatchdogBarking());
  }

  public void testBlown() throws InterruptedException {
    WatchdogInterface timedFuse = TimedFuse.barking(0.1); // 100[ms]
    assertTrue(timedFuse.isWatchdogBarking());
    Thread.sleep(10);
    assertTrue(timedFuse.isWatchdogBarking());
    timedFuse.notifyWatchdog();
    assertFalse(timedFuse.isWatchdogBarking());
    Thread.sleep(40);
    assertFalse(timedFuse.isWatchdogBarking());
    Thread.sleep(100);
    assertTrue(timedFuse.isWatchdogBarking());
    timedFuse.notifyWatchdog();
    assertFalse(timedFuse.isWatchdogBarking());
  }
}
