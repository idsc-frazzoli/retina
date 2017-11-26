// code by nisaak and jph
package ch.ethz.idsc.retina.dev.misc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;

/** misc information sent to micro-autobox */
public class MiscPutEvent extends DataEvent {
  private static final int LENGTH = 6;
  // ---
  /** table of values for resetConnection:
   * 0 - for normal operation
   * 1 - to acknowledge communication timeout */
  public byte resetConnection;
  public byte resetRimoL;
  public byte resetRimoR;
  public byte resetLinmot;
  public byte resetSteer;
  public byte ledControl;

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(resetConnection);
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
