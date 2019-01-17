// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class PenaltyTimeoutTest extends TestCase {
  public void testSimple() throws Exception {
    PenaltyTimeout penaltyTimeout = new PenaltyTimeout(0.01);
    assertTrue(penaltyTimeout.isBarking());
    penaltyTimeout.notifyWatchdog();
    assertFalse(penaltyTimeout.isBarking());
    Thread.sleep(10);
    assertTrue(penaltyTimeout.isBarking());
  }

  public void testFuse() throws Exception {
    Watchdog watchdog = SoftWatchdog.notified(0.01);
    assertFalse(watchdog.isBarking());
    watchdog.notifyWatchdog();
    assertFalse(watchdog.isBarking());
    Thread.sleep(10);
    assertTrue(watchdog.isBarking());
  }
}
