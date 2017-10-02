// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.io.Serializable;
import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.Clip;

/** information received from micro-autobox about the status of a motor usually
 * two of the events are received simultaneously: for the left and right rear
 * wheel */
public class RimoGetTire implements Serializable {
  /* package */ static final int LENGTH = 24;
  private static final Unit CELSIUS = Unit.of("degC");
  public static final Unit RATE_UNIT = Unit.of("rad*s^-1");
  // TODO NRJ check allowed ratings, comment magic const
  // TODO NRJ make emergency stop if too hot
  public static final Clip TEMPERATURE_RANGE = Clip.function( //
      Quantity.of(10, CELSIUS), //
      Quantity.of(80, CELSIUS));
  /** m */
  public static final double RADIUS = 0.14; // 14[cm] == 0.14[m]
  public static final double MIN_TO_S = 1 / 60.0;
  // ---
  public final short status_word;
  /** rad/min */
  private final short actual_rate;
  /** ARMS */
  public final short rms_motor_current;
  /** cV */
  private final short dc_bus_voltage;
  public final int error_code;
  /** C */
  private final short temperature_motor;
  /** C */
  private final short temperature_heatsink;
  public final SdoMessage sdoMessage;

  /** @param byteBuffer
   * of which 16 bytes are read */
  public RimoGetTire(ByteBuffer byteBuffer) {
    status_word = byteBuffer.getShort(); // 2
    actual_rate = byteBuffer.getShort(); // 4
    rms_motor_current = byteBuffer.getShort(); // 6
    dc_bus_voltage = byteBuffer.getShort(); // 8
    error_code = byteBuffer.getInt(); // 12
    temperature_motor = byteBuffer.getShort(); // 14
    temperature_heatsink = byteBuffer.getShort(); // 16
    sdoMessage = new SdoMessage(byteBuffer); // 24
  }

  void encode(ByteBuffer byteBuffer) {
    byteBuffer.putShort(status_word);
    byteBuffer.putShort(actual_rate);
    byteBuffer.putShort(rms_motor_current);
    byteBuffer.putShort(dc_bus_voltage);
    byteBuffer.putInt(error_code);
    byteBuffer.putShort(temperature_motor);
    byteBuffer.putShort(temperature_heatsink);
    sdoMessage.encode(byteBuffer);
  }

  /** @return convert rad/min to rad/s */
  public Scalar getAngularRate() {
    return Quantity.of(actual_rate * MIN_TO_S, RATE_UNIT);
  }

  public Scalar getBusVoltage() {
    return Quantity.of(dc_bus_voltage, "V");
  }

  public Scalar getTemperatureMotor() {
    // TODO NRJ right now only senses zeros
    return Quantity.of(temperature_motor, CELSIUS);
  }

  public Scalar getTemperatureHeatsink() {
    // TODO NRJ right now only senses zeros
    return Quantity.of(temperature_heatsink, CELSIUS);
  }

  public String toInfoString() {
    return String.format("%d %d %d %d %d %d %d", //
        status_word, actual_rate, //
        rms_motor_current, dc_bus_voltage, //
        error_code, //
        temperature_motor, temperature_heatsink);
  }
}
