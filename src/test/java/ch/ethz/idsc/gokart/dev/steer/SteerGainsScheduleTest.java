// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.tensor.RealScalar;
import junit.framework.TestCase;

public class SteerGainsScheduleTest extends TestCase {
  public void testSimple() {
    SteerGainsSchedule sgs = SteerGainsSchedule.INSTANCE;
    assertEquals(sgs.getTriple(RealScalar.of(10)), sgs.getTriple(RealScalar.of(25)));
  }
}
