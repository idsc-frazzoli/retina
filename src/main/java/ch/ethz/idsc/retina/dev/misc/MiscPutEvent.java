// code by nisaak and jph
package ch.ethz.idsc.retina.dev.misc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;

/** misc information sent to micro-autobox */
public class MiscPutEvent extends DataEvent {
  private static final int LENGTH = 5;
  // ---
  public byte resetRimoL;
  public byte resetRimoR;
  public byte resetLinmot;
  public byte resetSteer;
  public byte ledControl;

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(resetRimoL);
    byteBuffer.put(resetRimoR);
    byteBuffer.put(resetLinmot);
    byteBuffer.put(resetSteer);
    byteBuffer.put(ledControl);
  }

  @Override
  protected int length() {
    return LENGTH;
  }
}
