// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GokartStatusEventTest extends TestCase {
  public void testSimple() {
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(Float.NaN);
    assertFalse(gokartStatusEvent.isSteerColumnCalibrated());
    try {
      SteerConfig.getAngleFromSCE(gokartStatusEvent);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testUnitless() {
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(0.1f);
    assertTrue(gokartStatusEvent.isSteerColumnCalibrated());
    Scalar s = SteerConfig.getAngleFromSCE(gokartStatusEvent);
    assertFalse(s instanceof Quantity);
  }
}
