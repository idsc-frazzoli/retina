// code by jph
package ch.ethz.idsc.retina.dev.steer;

import junit.framework.TestCase;

public class SteerColumnTrackerTest extends TestCase {
  public void testSimple() {
    assertFalse(new SteerColumnTracker().isSteerColumnCalibrated());
  }

  public void testSimpleFail() {
    try {
      new SteerColumnTracker().getSteerColumnEncoderCentered();
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testMaxRange() {
    assertEquals(SteerColumnTracker.MAX_SCE.toString(), "0.6743167638778687[SCE]");
  }
}
