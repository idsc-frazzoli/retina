// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public enum RimoGetEvents {
  ;
  /** Example:
   * input of rateL == 1000, rateR == 1000 corresponds to a tangent speed of 2[m*s^-1]
   * 
   * @param rateL
   * @param rateR
   * @return */
  public static RimoGetEvent create(int rateL, int rateR) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[48]);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putShort(2, (short) -rateL);
    byteBuffer.putShort(2 + 24, (short) rateR);
    return new RimoGetEvent(byteBuffer);
  }
}
