// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.Word;

public enum RimoPutHelper {
  ;
  /** @param byteBuffer with order little endian
   * @return */
  public static final RimoPutEvent from(ByteBuffer byteBuffer) {
    return new RimoPutEvent(tire(byteBuffer), tire(byteBuffer));
  }

  /** @param byteBuffer
   * @return */
  private static RimoPutTire tire(ByteBuffer byteBuffer) {
    Word command = Word.createShort("", byteBuffer.getShort());
    short rate = byteBuffer.getShort();
    short torque = byteBuffer.getShort();
    RimoPutTire rimoPutTire = new RimoPutTire(command, rate, torque);
    rimoPutTire.trigger = byteBuffer.get();
    rimoPutTire.sdoCommand = byteBuffer.get();
    rimoPutTire.mainIndex = byteBuffer.getShort();
    rimoPutTire.subIndex = byteBuffer.get();
    rimoPutTire.sdoData = byteBuffer.getInt();
    return rimoPutTire;
  }
}
