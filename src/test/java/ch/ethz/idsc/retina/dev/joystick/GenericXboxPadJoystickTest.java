// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import junit.framework.TestCase;

public class GenericXboxPadJoystickTest extends TestCase {
  public void testLinmot() {
    GenericXboxPadJoystick joystick = new GenericXboxPadJoystick();
    byte[] array = new byte[20];
    for (int index = 0; index < array.length; ++index)
      array[index] = (byte) index;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getBreakStrength(), 0.031496062992125984);
    array[4] = 127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getBreakStrength(), 1.0);
    array[4] = -127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getBreakStrength(), 0.0);
    array[4] = -18;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getBreakStrength(), 0.0);
  }

  public void testSteer() {
    GenericXboxPadJoystick joystick = new GenericXboxPadJoystick();
    byte[] array = new byte[20];
    for (int index = 0; index < array.length; ++index)
      array[index] = (byte) index;
    array[3] = 127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getSteerLeft(), RealScalar.of(-1));
    array[3] = -127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getSteerLeft(), RealScalar.of(+1));
    array[3] = 0;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getSteerLeft(), RealScalar.ZERO);
  }

  public void testRimo() {
    GenericXboxPadJoystick joystick = new GenericXboxPadJoystick();
    byte[] array = new byte[20];
    for (int index = 0; index < array.length; ++index)
      array[index] = (byte) index;
    array[1] = 0;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getAheadAverage(), RealScalar.of(0));
    array[1] = 9;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getAheadAverage(), RealScalar.of(0));
    // the sign is toggled because the raw value encodes knob "down" instead of "up"
    array[1] = 10;
    joystick.decode(ByteBuffer.wrap(array));
    assertTrue(Scalars.nonZero(joystick.getAheadAverage()));
    assertEquals(joystick.getAheadAverage(), RealScalar.of(-0.0042553191489361625));
    array[1] = 127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getAheadAverage(), RealScalar.of(-1));
    array[1] = -127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getAheadAverage(), RealScalar.of(+1));
  }
}
