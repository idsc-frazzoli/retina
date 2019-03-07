// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import junit.framework.TestCase;

public class RimoEmergencyErrorsTest extends TestCase {
  public void testLength() {
    assertEquals(RimoEmergencyErrors.INSTANCE.size(), RimoEmergencyError.values().length);
  }
}
