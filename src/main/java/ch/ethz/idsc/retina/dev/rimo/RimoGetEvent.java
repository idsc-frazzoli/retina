// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;

/** information received from micro-autobox about the status of a motor usually
 * two of the events are received simultaneously: for the left and right rear
 * wheel */
public class RimoGetEvent {
  public static final int LENGTH = 16;
  // ---
  /** m */
  public static final double RADIUS = 0.14;
  public final short status_word;
  /** rad/min */
  public final short actual_speed;
  /** ARMS */
  public final short rms_motor_current;
  /** cV */
  private final short dc_bus_voltage;
  public final int error_code;
  /** C */
  public final short temperature_motor;
  /** C */
  public final short temperature_heatsink;

  /** @param byteBuffer
   * of which 16 bytes are read */
  public RimoGetEvent(ByteBuffer byteBuffer) {
    status_word = byteBuffer.getShort(); // 2
    actual_speed = byteBuffer.getShort(); // 4
    rms_motor_current = byteBuffer.getShort(); // 6
    dc_bus_voltage = byteBuffer.getShort(); // 8
    error_code = byteBuffer.getInt(); // 12
    temperature_motor = byteBuffer.getShort(); // 14
    temperature_heatsink = byteBuffer.getShort(); // 16
  }

  public void encode(ByteBuffer byteBuffer) {
    byteBuffer.putShort(status_word);
    byteBuffer.putShort(actual_speed);
    byteBuffer.putShort(rms_motor_current);
    byteBuffer.putShort(dc_bus_voltage);
    byteBuffer.putInt(error_code);
    byteBuffer.putShort(temperature_motor);
    byteBuffer.putShort(temperature_heatsink);
  }

  /** @return convert rad/min to m/s */
  public double getActualSpeed() {
    return actual_speed * RADIUS / 60;
  }

  public static short getRawValueSpeed(double speed) {
    return (short) Math.round(speed * 60 / RADIUS);
  }

  public int getBusVoltage() {
    return dc_bus_voltage;
  }

  public float getTemperatureMotor() {
    return temperature_motor; // TODO right now only senses zeros
  }

  // TODO NRJ provide functions that compute physical values from raw integer
  // values
  // for instance voltage, current, temperature...
  public String toInfoString() {
    return String.format("%d %d %d %d %d %d %d", //
        status_word, actual_speed, //
        rms_motor_current, dc_bus_voltage, //
        error_code, //
        temperature_motor, temperature_heatsink);
  }
}
