// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public enum SteerGetHelper {
  ;
  public static SteerGetEvent create(float value, float rckQual) {
    byte[] array = new byte[44];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putFloat(0);
    byteBuffer.putFloat(0);
    byteBuffer.putFloat(0);
    byteBuffer.putFloat(0);
    byteBuffer.putFloat(0);
    // ---
    byteBuffer.putFloat(0);
    byteBuffer.putFloat(0);
    byteBuffer.putFloat(value);
    byteBuffer.putFloat(rckQual);
    byteBuffer.putFloat(0);
    byteBuffer.putFloat(0);
    byteBuffer.flip();
    return new SteerGetEvent(byteBuffer);
  }

  public static SteerGetEvent create(float value) {
    return create(value, SteerGetStatus.OPERATIONAL.value());
  }
}
