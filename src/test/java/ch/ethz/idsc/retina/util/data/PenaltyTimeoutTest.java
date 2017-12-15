// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class PenaltyTimeoutTest extends TestCase {
  public void testSimple() throws Exception {
    PenaltyTimeout pt = new PenaltyTimeout(0.01);
    assertFalse(pt.isPenalty());
    pt.flagPenalty();
    assertTrue(pt.isPenalty());
    Thread.sleep(10);
    assertFalse(pt.isPenalty());
  }
}
