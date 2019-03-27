// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LinmotGetEventsTest extends TestCase {
  public void testSimple() {
    LinmotGetEvent linmotGetEvent = LinmotGetEvents.ZEROS;
    assertFalse(linmotGetEvent.isOperational());
    assertEquals(linmotGetEvent.getActualPosition(), Quantity.of(0, SI.METER));
  }
}
