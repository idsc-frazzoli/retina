// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import junit.framework.TestCase;

public class SteerColumnEventsTest extends TestCase {
  public void testSimple() {
    assertFalse(SteerColumnEvents.UNKNOWN.isSteerColumnCalibrated());
  }
}
