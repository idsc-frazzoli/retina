// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public enum MiscGetEventSimulator {
  ;
  public static MiscGetEvent create(byte emg, float voltage_raw) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[5]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.put(emg);
    byteBuffer.putFloat(voltage_raw);
    byteBuffer.flip();
    return new MiscGetEvent(byteBuffer);
  }
}
