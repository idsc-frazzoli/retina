// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;

public class RimoGetEvent {
  public final short status_word;
  /** rad/min */
  public final short actual_speed;
  /** ARMS */
  public final short rms_motor_current;
  /** cV */
  public final short dc_bus_voltage;
  public final short error_code;
  /** C */
  public final short temperature_motor;
  /** C */
  public final short temperature_heatsink;

  /** @param byteBuffer of which 14 bytes are read */
  public RimoGetEvent(ByteBuffer byteBuffer) {
    status_word = byteBuffer.getShort(); // 2
    actual_speed = byteBuffer.getShort(); // 4
    rms_motor_current = byteBuffer.getShort(); // 6
    dc_bus_voltage = byteBuffer.getShort(); // 8
    error_code = byteBuffer.getShort(); // 10
    temperature_motor = byteBuffer.getShort(); // 12
    temperature_heatsink = byteBuffer.getShort(); // 14
  }
}
