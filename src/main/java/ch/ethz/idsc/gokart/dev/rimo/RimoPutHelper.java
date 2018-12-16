// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.Word;

/** decoding of rimo put event message in lcm client and offline processing */
public enum RimoPutHelper {
  ;
  /** @param byteBuffer with order little endian and at least 30 bytes remaining
   * @return */
  public static RimoPutEvent from(ByteBuffer byteBuffer) {
    return new RimoPutEvent( //
        tire(byteBuffer), //
        tire(byteBuffer));
  }

  /** @param byteBuffer with at least 15 bytes remaining
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

  /** Important: when applying torques of same sign and no resistance on the
   * motors, the wheels spin in opposite directions.
   * 
   * The input to the function is treated as sign corrected. For instance,
   * for forward driving, the torques have alternating signs, in particular
   * <pre>
   * armsL_raw < 0
   * armsR_raw > 0
   * </pre>
   * 
   * @param armsL_raw between -2317 and +2316
   * @param armsR_raw between -2317 and +2316
   * @return */
  public static RimoPutEvent operationTorque(short armsL_raw, short armsR_raw) {
    return new RimoPutEvent( //
        new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsL_raw), //
        new RimoPutTire(RimoPutTire.OPERATION, (short) 0, armsR_raw) //
    );
  }
}
