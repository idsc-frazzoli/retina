// code by jph
package ch.ethz.idsc.retina.dev.joystick;

import java.nio.ByteBuffer;

public enum JoystickDecoder {
  ;
  public static AbstractJoystick decode(ByteBuffer byteBuffer) {
    int ordinal = byteBuffer.get() & 0xff;
    AbstractJoystick abstractJoystick = JoystickType.values()[ordinal].supplier.get();
    abstractJoystick.decode(byteBuffer);
    return abstractJoystick;
  }
}
