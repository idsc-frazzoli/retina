// code by jph
package ch.ethz.idsc.retina.joystick;

import java.nio.ByteBuffer;

public enum JoystickDecoder {
  ;
  public static JoystickEvent decode(ByteBuffer byteBuffer) {
    int ordinal = byteBuffer.get() & 0xff; // joystick device id
    JoystickEvent joystickEvent = JoystickType.values()[ordinal].supplier.get();
    joystickEvent.decode(byteBuffer);
    return joystickEvent;
  }
}
