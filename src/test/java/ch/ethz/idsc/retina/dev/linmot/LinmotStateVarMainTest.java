// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import junit.framework.TestCase;

public class LinmotStateVarMainTest extends TestCase {
  public void testOrdinal() {
    assertEquals(LinmotStateVarMain.OPERATION_ENABLED.ordinal(), 8);
    assertEquals(LinmotStateVarMain.SPECIAL_MODE.ordinal(), 20);
    assertEquals(LinmotStateVarMain.BRAKE_DELAY.ordinal(), 21);
  }
}
