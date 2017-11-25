// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MiscGetEvent extends DataEvent {
  // maps the output of the ADC(0-1) to the real input voltage (0V-5V)
  // 2.8 accounts for the resitance value of the voltage divider
  private static final double CONVERSION_V = 5 * 2.8; // 5 * 2.8
  // ---
  public static final int LENGTH = 5;
  // ---
  private final byte emergency;
  private final float batteryAdc;

  /** the byteBuffer has the following content
   * byte emergency (== 0 if no problem)
   * float battery adc
   * 
   * @param byteBuffer */
  public MiscGetEvent(ByteBuffer byteBuffer) {
    emergency = byteBuffer.get();
    batteryAdc = byteBuffer.getFloat();
  }

  public boolean isEmergency() {
    return emergency != 0;
  }

  public boolean isCommTimeout() {
    int mask = 1 << MiscEmergencyBit.COMM_TIMEOUT.ordinal();
    return (emergency & mask) == mask;
  }

  /** @return emergency status byte
   * @see MiscEmergencyBit */
  public byte getEmergency() {
    return emergency;
  }

  /** @return the voltage of the front battery in volts */
  public Scalar getSteerBatteryVoltage() {
    return Quantity.of(batteryAdc * CONVERSION_V, "V");
  }

  @Override
  protected void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(emergency);
    byteBuffer.putFloat(batteryAdc);
  }

  @Override
  protected int length() {
    return LENGTH;
  }
}
