// code by jph
package ch.ethz.idsc.retina.joystick;

import java.nio.ByteBuffer;

import junit.framework.TestCase;

public class JoystickDecoderTest extends TestCase {
  public void testSupply() {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[21]);
    JoystickEvent joystickEvent = JoystickDecoder.decode(byteBuffer);
    assertTrue(joystickEvent instanceof GenericXboxPadJoystick);
  }
}
