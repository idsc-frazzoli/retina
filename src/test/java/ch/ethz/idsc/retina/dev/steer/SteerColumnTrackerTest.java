// code by jph
package ch.ethz.idsc.retina.dev.steer;

import junit.framework.TestCase;

public class SteerColumnTrackerTest extends TestCase {
  public void testSimple() {
    assertFalse(new SteerColumnTracker().isCalibrated());
  }

  public void testSimpleFail() {
    try {
      new SteerColumnTracker().getEncoderValueCentered();
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
