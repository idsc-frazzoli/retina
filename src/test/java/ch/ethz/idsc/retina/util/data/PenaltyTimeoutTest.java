// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class PenaltyTimeoutTest extends TestCase {
  public void testSimple() throws Exception {
    PenaltyTimeout penaltyTimeout = new PenaltyTimeout(0.01);
    assertFalse(penaltyTimeout.isPenalty());
    penaltyTimeout.flagPenalty();
    assertTrue(penaltyTimeout.isPenalty());
    Thread.sleep(10);
    assertFalse(penaltyTimeout.isPenalty());
  }
}
