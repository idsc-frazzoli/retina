// code by jph
package ch.ethz.idsc.gokart.dev.misc;

import junit.framework.TestCase;

public class MiscEmergencyBitTest extends TestCase {
  public void testSimple() {
    assertTrue(MiscEmergencyBit.COMM_TIMEOUT.isActive((byte) 1));
    assertFalse(MiscEmergencyBit.COMM_TIMEOUT.isActive((byte) 2));
  }
}
