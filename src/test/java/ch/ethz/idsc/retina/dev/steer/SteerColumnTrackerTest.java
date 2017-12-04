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
    assertEquals(SteerConfig.GLOBAL.columnMax.toString(), "0.6[SCE]");
  }
}
