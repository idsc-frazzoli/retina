// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import junit.framework.TestCase;

public class GokartStatusEventsTest extends TestCase {
  public void testSimple() {
    assertFalse(GokartStatusEvents.UNKNOWN.isSteerColumnCalibrated());
  }
}
