// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class RimoPutEvent implements Serializable {
  public static final int LENGTH = 2 * RimoPutTire.LENGTH;
  public static final RimoPutEvent STOP = new RimoPutEvent( //
      RimoPutTire.STOP, //
      RimoPutTire.STOP);
  // ---
  public final RimoPutTire putL;
  public final RimoPutTire putR;

  public RimoPutEvent(RimoPutTire putL, RimoPutTire putR) {
    this.putL = putL;
    this.putR = putR;
  }

  /* package */ void insert(ByteBuffer byteBuffer) {
    byteBuffer.putShort(putL.command);
    byteBuffer.putShort(putL.speed);
    byteBuffer.putShort(putR.command);
    byteBuffer.putShort(putR.speed);
  }
}
