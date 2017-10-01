// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;

public class RimoPutEvent extends DataEvent {
  private static final int LENGTH = 2 * RimoPutTire.LENGTH;
  public static final RimoPutEvent STOP = new RimoPutEvent( //
      RimoPutTire.STOP, //
      RimoPutTire.STOP);

  public static RimoPutEvent withSpeeds(short velL, short velR) {
    return new RimoPutEvent( //
        RimoPutTire.withSpeed(velL), //
        RimoPutTire.withSpeed(velR)); //
  }

  // ---
  public final RimoPutTire putL;
  public final RimoPutTire putR;

  public RimoPutEvent(RimoPutTire putL, RimoPutTire putR) {
    this.putL = putL;
    this.putR = putR;
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    putL.insert(byteBuffer);
    putR.insert(byteBuffer);
  }

  @Override
  protected int length() {
    return LENGTH;
  }
}
