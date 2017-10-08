// code by jph
package ch.ethz.idsc.retina.dev.steer;

import junit.framework.TestCase;

public class SteerAngleTrackerTest extends TestCase {
  public void testSimple() {
    assertFalse(new SteerAngleTracker().isCalibrated());
  }

  public void testSimpleFail() {
    try {
      new SteerAngleTracker().getSteeringValue();
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
