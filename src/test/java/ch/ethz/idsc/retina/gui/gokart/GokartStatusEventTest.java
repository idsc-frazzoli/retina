// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GokartStatusEventTest extends TestCase {
  public void testSimple() {
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(Float.NaN);
    assertFalse(gokartStatusEvent.isSteerColumnCalibrated());
    try {
      gokartStatusEvent.getSteeringAngle();
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testUnitless() {
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(0.1f);
    assertTrue(gokartStatusEvent.isSteerColumnCalibrated());
    Scalar s = gokartStatusEvent.getSteeringAngle();
    assertFalse(s instanceof Quantity);
  }
}
