// code by jph
package ch.ethz.idsc.retina.dev.steer;

import junit.framework.TestCase;

public class SteerColumnTrackerTest extends TestCase {
  public void testSimple() {
    assertFalse(new SteerColumnTracker().isCalibrated());
  }

  public void testSimpleFail() {
    try {
      new SteerColumnTracker().getSteeringValue();
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
