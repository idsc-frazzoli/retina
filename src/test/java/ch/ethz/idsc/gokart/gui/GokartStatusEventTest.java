// code by jph
package ch.ethz.idsc.gokart.gui;

import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class GokartStatusEventTest extends TestCase {
  public void testSimple() {
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(Float.NaN);
    assertFalse(gokartStatusEvent.isSteerColumnCalibrated());
    try {
      SteerConfig.GLOBAL.getAngleFromSCE(gokartStatusEvent);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testUnitless() {
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(0.1f);
    assertTrue(gokartStatusEvent.isSteerColumnCalibrated());
    Scalar scalar = SteerConfig.GLOBAL.getAngleFromSCE(gokartStatusEvent);
    assertFalse(scalar instanceof Quantity);
    Clip.function(0.05, 0.08).requireInside(scalar);
  }
}
