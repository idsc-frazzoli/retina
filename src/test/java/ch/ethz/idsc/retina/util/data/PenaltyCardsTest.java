// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class PenaltyCardsTest extends TestCase {
  public void testSimple() {
    PenaltyCards penaltyCards = new PenaltyCards();
    assertFalse(penaltyCards.isPenalty());
    penaltyCards.evaluate(true, false);
    assertFalse(penaltyCards.isPenalty());
    penaltyCards.evaluate(false, false);
    assertFalse(penaltyCards.isPenalty());
    penaltyCards.evaluate(true, true);
    assertTrue(penaltyCards.isPenalty());
    penaltyCards.evaluate(true, false);
    assertTrue(penaltyCards.isPenalty());
    penaltyCards.evaluate(false, false);
    assertFalse(penaltyCards.isPenalty());
  }
}
