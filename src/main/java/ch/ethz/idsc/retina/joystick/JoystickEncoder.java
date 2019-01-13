// code by jph
package ch.ethz.idsc.retina.joystick;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public enum JoystickEncoder {
  ;
  public static void encode( //
      JoystickType joystickType, FloatBuffer axes, ByteBuffer buttons, ByteBuffer hats, ByteBuffer dest) {
    dest.put((byte) joystickType.ordinal()); // joystick id
    while (axes.hasRemaining()) // <- null pointer exception has been observed here
      dest.put((byte) (axes.get() * Byte.MAX_VALUE));
    {
      short mask = 0; // no more than 16 buttons
      /** joystickType.buttons may be less than buttons.limit() because lwjgl may
       * encoded hats directions as buttons */
      for (int index = 0; index < joystickType.buttons; ++index) {
        int value = buttons.get() & 1;
        mask |= value << index;
      }
      dest.putShort(mask);
      // System.out.println(String.format("%04X", mask));
    }
    while (hats.hasRemaining()) {
      byte value = hats.get();
      dest.put(value);
      // System.out.println(String.format("%02X", value & 0xff));
    }
  }
}
