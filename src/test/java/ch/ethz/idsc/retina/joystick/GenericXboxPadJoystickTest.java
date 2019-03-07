// code by jph
package ch.ethz.idsc.retina.joystick;

import java.nio.ByteBuffer;
import java.util.Objects;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GenericXboxPadJoystickTest extends TestCase {
  public void testLinmot() {
    GenericXboxPadJoystick joystick = GenericXboxPadJoystick.createDefault();
    byte[] array = new byte[20];
    for (int index = 0; index < array.length; ++index)
      array[index] = (byte) index;
    array[2] = -127;
    array[5] = -127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getBreakStrength(), RealScalar.of(0.031496062992125984));
    array[4] = 127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getBreakStrength(), RealScalar.of(1.0));
    array[4] = -127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getBreakStrength(), RealScalar.ZERO);
    array[4] = -18;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getBreakStrength(), RealScalar.ZERO);
  }

  public void testSteer() {
    GenericXboxPadJoystick joystick = GenericXboxPadJoystick.createDefault();
    byte[] array = new byte[20];
    for (int index = 0; index < array.length; ++index)
      array[index] = (byte) index;
    array[2] = -127;
    array[5] = -127;
    joystick.decode(ByteBuffer.wrap(array));
    // ---
    array[3] = 127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getSteerLeft(), RealScalar.of(-1));
    array[3] = -127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getSteerLeft(), RealScalar.of(+1));
    // ---
    array[3] = 0;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getSteerLeft(), RealScalar.ZERO);
  }

  public void testRimoAverage() {
    GenericXboxPadJoystick joystick = GenericXboxPadJoystick.createDefault();
    byte[] array = new byte[20];
    for (int index = 0; index < array.length; ++index)
      array[index] = (byte) index;
    array[2] = -127;
    array[5] = -127;
    assertTrue(Objects.nonNull(joystick.toString()));
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

  public void testRimoPair() {
    GenericXboxPadJoystick joystick = GenericXboxPadJoystick.createDefault();
    byte[] array = new byte[20];
    for (int index = 0; index < array.length; ++index)
      array[index] = (byte) index;
    array[2] = 0;
    array[5] = 0;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getAheadPair_Unit(), Tensors.vector(0.5, 0.5));
    array[2] = 127;
    array[5] = 127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getAheadPair_Unit(), Tensors.vector(1.0, 1.0));
    array[2] = -127;
    array[5] = -127;
    joystick.decode(ByteBuffer.wrap(array));
    assertEquals(joystick.getAheadPair_Unit(), Tensors.vector(0.0, 0.0));
  }
}
