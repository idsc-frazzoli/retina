// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import junit.framework.TestCase;

public class JoystickTypeTest extends TestCase {
  public void testSimple() {
    assertEquals(JoystickType.GENERIC_XBOX_PAD.encodingSize(), 10);
  }
}
