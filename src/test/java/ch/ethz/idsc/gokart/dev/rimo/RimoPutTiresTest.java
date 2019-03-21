// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import junit.framework.TestCase;

public class RimoPutTiresTest extends TestCase {
  public void testValid() {
    assertTrue(RimoPutTires.isTorqueValid(+123));
    assertTrue(RimoPutTires.isTorqueValid(-123));
    assertTrue(RimoPutTires.isTorqueValid(+1230));
    assertTrue(RimoPutTires.isTorqueValid(-1230));
    assertTrue(RimoPutTires.isTorqueValid(+2316));
    assertTrue(RimoPutTires.isTorqueValid(-2316));
    assertFalse(RimoPutTires.isTorqueValid(+2317));
    assertFalse(RimoPutTires.isTorqueValid(-2317));
  }
}
