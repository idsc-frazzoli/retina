// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import junit.framework.TestCase;

public class RimoEmergencyErrorsTest extends TestCase {
  public void testLength() {
    assertEquals(RimoEmergencyErrors.INSTANCE.size(), RimoEmergencyError.values().length);
  }
}
