// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import junit.framework.TestCase;

public class ControlAndPredictionStepTest extends TestCase {
  public void testSimple() {
    assertEquals(ControlAndPredictionStep.LENGTH, 64);
  }
}
