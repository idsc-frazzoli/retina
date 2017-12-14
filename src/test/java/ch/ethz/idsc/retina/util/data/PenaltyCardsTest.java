// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class PenaltyCardsTest extends TestCase {
  public void testSimple() {
    PenaltyCards pc = new PenaltyCards();
    assertFalse(pc.isPenalty());
    pc.evaluate(true, false);
    assertFalse(pc.isPenalty());
    pc.evaluate(false, false);
    assertFalse(pc.isPenalty());
    pc.evaluate(true, true);
    assertTrue(pc.isPenalty());
    pc.evaluate(true, false);
    assertTrue(pc.isPenalty());
    pc.evaluate(false, false);
    assertFalse(pc.isPenalty());
  }
}
