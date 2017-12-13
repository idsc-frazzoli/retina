// code by jph
package ch.ethz.idsc.retina.dev.steer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class SteerColumnTrackerTest extends TestCase {
  public void testSimple() {
    SteerColumnTracker steerColumnTracker = new SteerColumnTracker();
    assertFalse(steerColumnTracker.isSteerColumnCalibrated());
    assertFalse(steerColumnTracker.isCalibratedAndHealthy());
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

  public void testFeed() {
    SteerColumnTracker steerColumnTracker = new SteerColumnTracker();
    steerColumnTracker.getEvent(SteerGetHelper.create(+0.75f));
    steerColumnTracker.getEvent(SteerGetHelper.create(-0.75f));
    assertTrue(steerColumnTracker.isSteerColumnCalibrated());
    assertTrue(steerColumnTracker.isCalibratedAndHealthy());
    Scalar scalar = steerColumnTracker.getSteerColumnEncoderCentered();
    assertTrue(scalar instanceof Quantity);
    steerColumnTracker.getEvent(SteerGetHelper.create(+0.95f));
    assertTrue(steerColumnTracker.isSteerColumnCalibrated());
    assertFalse(steerColumnTracker.isCalibratedAndHealthy());
  }
}
