// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public enum RimoGetEvents {
  ;
  public static RimoGetEvent create(int rateL, int rateR) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putShort(2, (short) -rateL);
    byteBuffer.putShort(2 + 24, (short) rateR);
    return new RimoGetEvent(byteBuffer);
  }
}
