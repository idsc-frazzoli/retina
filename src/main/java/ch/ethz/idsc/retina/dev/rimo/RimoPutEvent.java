// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class RimoPutEvent implements Serializable {
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
