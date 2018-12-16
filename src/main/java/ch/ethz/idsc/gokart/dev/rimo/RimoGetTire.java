// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Optional;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.math.SIDerived;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/** information received from micro-autobox about the status of a motor usually
 * two of the events are received simultaneously: for the left and right rear wheel
 * 
 * LONGTERM NRJ temperature readings non-zero, check allowed ratings
 * LONGTERM NRJ the meaning of the error_code still has to be determined */
public class RimoGetTire implements Serializable {
  /* package */ static final int LENGTH = 24;
  public static final double MIN_TO_S = 1 / 60.0;
  // ---
  public final short status_word;
  /** actual_rate has unit rad/min */
  // ACTUAL_RATE SHALL BE PRIVATE DUE TO THE REQUIRED SIGN CORRECTION
  // NEVER CONSIDER USING THE RAW VALUE OUTSIDE THIS CLASS !
  private final short actual_rate; // STRICTLY PRIVATE !
  /** ARMS */
  public final short rms_motor_current;
  /** cV */
  private final short dc_bus_voltage;
  /** observed examples when engines have just been started:
   * _LEFT 0x23400008
   * RIGHT 0x23100008
   * the motors are operational despite these readings. */
  public final int error_code;
  /** C */
  private final short temperature_motor;
  /** C */
  private final short temperature_heatsink;
  public final SdoMessage sdoMessage;
  private final int sign;

  /** @param byteBuffer of which 24 bytes are read
   * @param sign to standardize the angular rate around the y-axis,
   * the current firmware on the micro-autobox requires
   * -1 for the LEFT wheel, and +1 for the RIGHT wheel */
  RimoGetTire(ByteBuffer byteBuffer, int sign) {
    status_word = byteBuffer.getShort(); // 0:2
    actual_rate = byteBuffer.getShort(); // 2:4
    rms_motor_current = byteBuffer.getShort(); // 4:6
    dc_bus_voltage = byteBuffer.getShort(); // 6:8
    error_code = byteBuffer.getInt(); // 8:12
    temperature_motor = byteBuffer.getShort(); // 12:14
    temperature_heatsink = byteBuffer.getShort(); // 14:16
    sdoMessage = new SdoMessage(byteBuffer); // 16:24
    // ---
    this.sign = sign;
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

  /** @return rotational rate around the y-axis with unit rad/s
   * a positive value usually corresponds to forward motion of the vehicle */
  public Scalar getAngularRate_Y() {
    return Quantity.of(actual_rate * sign * MIN_TO_S, SIDerived.RADIAN_PER_SECOND);
  }

  /** the value of RmsMotorCurrent is not correlated to the expected motor current.
   * 
   * @return */
  public Scalar getRmsMotorCurrent() {
    return Quantity.of(rms_motor_current, NonSI.ARMS);
  }

  public Scalar getBusVoltage() {
    return Quantity.of(dc_bus_voltage, SI.VOLT);
  }

  /** @return 0[degC] */
  public Scalar getTemperatureMotor() {
    return Quantity.of(temperature_motor, NonSI.DEGREE_CELSIUS);
  }

  /** @return 0[degC] */
  public Scalar getTemperatureHeatsink() {
    return Quantity.of(temperature_heatsink, NonSI.DEGREE_CELSIUS);
  }

  public Optional<RimoEmergencyError> getEmergencyError() {
    /** documentation on the definite decoding of the error code is not available
     * we suspect the 2 hi bytes are the emergency error code */
    return RimoEmergencyErrors.INSTANCE.ofCode((short) (error_code >> 16));
  }

  public String toInfoString() {
    return String.format("%d %d %d %d %d %d %d", //
        status_word, actual_rate, //
        rms_motor_current, dc_bus_voltage, //
        error_code, //
        temperature_motor, temperature_heatsink);
  }

  public int getErrorCodeMasked() {
    return error_code & 0x00ffffff;
  }

  /* package */ Tensor asVector() {
    return Tensors.vector( //
        status_word, //
        actual_rate * sign * MIN_TO_S, //
        rms_motor_current, //
        dc_bus_voltage, //
        error_code, //
        temperature_motor, //
        temperature_heatsink);
  }
}
