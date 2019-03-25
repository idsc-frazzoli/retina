// code by jph
package ch.ethz.idsc.retina.imu.vmu931;

import junit.framework.TestCase;

public class Vmu931StaticsTest extends TestCase {
  public void testCalibration() {
    byte[] calibration = Vmu931Statics.requestCalibration();
    assertEquals(calibration[0], 'v');
    assertEquals(calibration[1], 'a');
    assertEquals(calibration[2], 'r');
    assertEquals(calibration[3], 'l');
  }
}
