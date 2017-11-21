// code by jph
package ch.ethz.idsc.retina.util.math;

import junit.framework.TestCase;

public class IntervalTrackerTest extends TestCase {
  public void testSimple() {
    IntervalTracker intervalTracker = new IntervalTracker();
    assertTrue(Double.isNaN(intervalTracker.getValue()));
    assertTrue(Double.isNaN(intervalTracker.getValueCentered()));
    assertTrue(Double.isInfinite(intervalTracker.getWidth()));
  }

  public void testActive() {
    IntervalTracker it = new IntervalTracker();
    it.setValue(4);
    assertEquals(it.getValue(), 4.0);
    assertEquals(it.getWidth(), 0.0);
    it.setValue(4);
    assertEquals(it.getWidth(), 0.0);
    assertEquals(it.getValueCentered(), 0.0);
    it.setValue(5);
    assertEquals(it.getValueCentered(), 0.5);
    assertEquals(it.getWidth(), 1.0);
    it.setValue(6);
    assertEquals(it.getValueCentered(), 1.0);
    it.setValue(2);
    assertEquals(it.getValueCentered(), -2.0);
    assertEquals(it.getValue(), 2.0);
  }
}
