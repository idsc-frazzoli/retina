// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MiscGetEvent extends DataEvent {
  public static final int LENGTH = 5;
  // ---
  private final byte emergency;
  private final float batteryAdc;

  public MiscGetEvent(ByteBuffer byteBuffer) {
    emergency = byteBuffer.get();
    batteryAdc = byteBuffer.getFloat();
  }

  public boolean isEmergency() {
    return emergency != 0;
  }

  /** @return the voltage of the front battery in volts */
  public Scalar getSteerBatteryVoltage() {
    // maps the output of the ADC(0-1) to the real input voltage (0V-5V)
    // 2.8 accounts for the resitance value of the voltage divider
    // TODO NRJ MAC state ohm of resistors
    return Quantity.of(batteryAdc * 5 * 2.8, "V");
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
