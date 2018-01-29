// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class TriggeredTimeIntervalTest extends TestCase {
  public void testSimple() throws Exception {
    TriggeredTimeInterval triggeredInterval = new TriggeredTimeInterval(0.05);
    assertFalse(triggeredInterval.isActive());
    Thread.sleep(10);
    assertFalse(triggeredInterval.isActive());
    triggeredInterval.panic();
    assertTrue(triggeredInterval.isActive());
    Thread.sleep(1);
    assertTrue(triggeredInterval.isActive());
    Thread.sleep(1);
    assertTrue(triggeredInterval.isActive());
    Thread.sleep(50);
    assertFalse(triggeredInterval.isActive());
    triggeredInterval.panic();
    assertFalse(triggeredInterval.isActive());
  }
}
