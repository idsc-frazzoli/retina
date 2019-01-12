// code by jph
package ch.ethz.idsc.retina.joystick;

import junit.framework.TestCase;

public class JoystickTypeTest extends TestCase {
  public void testSimple() {
    assertEquals(JoystickType.GENERIC_XBOX_PAD.encodingSize(), 10);
  }

  public void testString() {
    assertEquals(String.format("%02x", (byte) -1), "ff");
  }
}
