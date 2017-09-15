// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public enum JoystickEncoder {
  ;
  public static void encode( //
      JoystickType joystickType, FloatBuffer axes, ByteBuffer buttons, ByteBuffer hats, ByteBuffer dest) {
    dest.put((byte) joystickType.ordinal()); // joystick id
    while (axes.hasRemaining())
      dest.put((byte) (axes.get() * Byte.MAX_VALUE));
    {
      short mask = 0; // no more than 16 buttons
      int index = 0;
      while (buttons.hasRemaining()) {
        int value = buttons.get() & 1;
        mask |= value << index++;
      }
      dest.putShort(mask);
    }
    while (hats.hasRemaining())
      dest.put(hats.get());
  }
}
