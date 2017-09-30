// code by jph
package ch.ethz.idsc.retina.dev.steer;

import junit.framework.TestCase;

public class SteerAngleTrackerTest extends TestCase {
  public void testSimple() {
    assertFalse(SteerAngleTracker.INSTANCE.isCalibrated());
  }
}
