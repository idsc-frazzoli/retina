// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class RimoGetEventsTest extends TestCase {
  public void testMotionless() {
    RimoGetEvent rimoGetEvent = RimoGetEvents.motionless();
    assertEquals(rimoGetEvent.getAngularRate_Y_pair(), Tensors.fromString("{0.0[s^-1], 0.0[s^-1]}"));
  }
}
