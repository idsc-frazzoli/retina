// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class GokartStatusEventTest extends TestCase {
  public void testSimple() {
    GokartStatusEvent gokartStatusEvent = new GokartStatusEvent(Float.NaN);
    assertFalse(gokartStatusEvent.isSteeringCalibrated());
    Scalar s = gokartStatusEvent.getSteeringAngle();
    assertFalse(s instanceof Quantity);
    assertFalse(NumberQ.of(s));
    assertEquals(s.toString(), "NaN");
  }
}
