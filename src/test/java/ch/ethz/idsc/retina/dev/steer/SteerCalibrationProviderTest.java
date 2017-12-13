// code by jph
package ch.ethz.idsc.retina.dev.steer;

import junit.framework.TestCase;

public class SteerCalibrationProviderTest extends TestCase {
  public void testSimple() {
    SteerCalibrationProvider.INSTANCE.protected_schedule();
  }
}
