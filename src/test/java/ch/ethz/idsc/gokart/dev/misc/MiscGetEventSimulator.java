// code by jph
package ch.ethz.idsc.gokart.dev.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public enum MiscGetEventSimulator {
  ;
  private static final double CONVERSION_V = 5 * 2.8;

  public static MiscGetEvent create(byte emg, float voltage_raw) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.put(emg);
    byteBuffer.putFloat(voltage_raw);
    byteBuffer.flip();
    return new MiscGetEvent(byteBuffer);
  }

  public static MiscGetEvent createVoltage(double voltage) {
    return create((byte) 0, (float) (voltage / CONVERSION_V));
  }
}
