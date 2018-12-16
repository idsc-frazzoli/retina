// code by jph
package ch.ethz.idsc.gokart.dev.misc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MiscGetEvent extends DataEvent {
  // maps the output of the ADC(0-1) to the real input voltage (0V-5V)
  // 2.8 accounts for the resistance value of the voltage divider
  private static final double CONVERSION_V = 5 * 2.8;
  /* package */ static final int LENGTH = 5;
  // ---
  private final byte emergency;
  private final float batteryAdc;

  /** the byteBuffer has the following content
   * byte emergency (== 0 if no problem)
   * float battery adc
   * 
   * @param byteBuffer from which constructor reads 5 bytes */
  public MiscGetEvent(ByteBuffer byteBuffer) {
    emergency = byteBuffer.get();
    batteryAdc = byteBuffer.getFloat();
  }

  /** @return true if software reset is required */
  public boolean isEmergency() {
    return emergency != 0;
  }

  /** @return true if software reset is required */
  public boolean isCommTimeout() {
    return MiscEmergencyBit.COMM_TIMEOUT.isActive(emergency);
  }

  /** @return emergency status byte
   * @see MiscEmergencyBit */
  public byte getEmergency() {
    return emergency;
  }

  /** @return the voltage of the front battery in unit volts "V" */
  public Scalar getSteerBatteryVoltage() {
    return Quantity.of(batteryAdc * CONVERSION_V, SI.VOLT);
  }

  @Override // from DataEvent
  protected void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(emergency);
    byteBuffer.putFloat(batteryAdc);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector( //
        emergency & 0xff, //
        batteryAdc * CONVERSION_V //
    );
  }
}
