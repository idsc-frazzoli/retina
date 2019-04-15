// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import junit.framework.TestCase;

public class RimoEmergencyErrorTest extends TestCase {
  public void testLength() {
    assertEquals(RimoEmergencyError.AC_CURRENT_OVER_CURRENT.ordinal(), 0);
  }
}
