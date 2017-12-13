// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public enum SteerGetHelper {
  ;
  public static SteerGetEvent create(float value) {
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
    byteBuffer.putFloat(0);
    byteBuffer.putFloat(0);
    byteBuffer.putFloat(0);
    byteBuffer.flip();
    return new SteerGetEvent(byteBuffer);
  }
}
