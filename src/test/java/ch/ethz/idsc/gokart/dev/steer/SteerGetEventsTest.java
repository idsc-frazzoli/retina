// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class SteerGetEventsTest extends TestCase {
  public void testSimple() {
    assertFalse(SteerGetEvents.ZEROS.isActive());
  }

  public void testZeroTorques() {
    assertTrue(Scalars.isZero(SteerGetEvents.ZEROS.refMotTrq()));
    assertTrue(Scalars.isZero(SteerGetEvents.ZEROS.estMotTrq()));
    assertTrue(Scalars.isZero(SteerGetEvents.ZEROS.tsuTrq()));
  }
}
