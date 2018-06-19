// code by jph
package ch.ethz.idsc.owl.car.math;

import junit.framework.TestCase;

public class EmptyClearanceTrackerTest extends TestCase {
  public void testSimple() {
    assertFalse(EmptyClearanceTracker.INSTANCE.isObstructed(null));
  }
}
