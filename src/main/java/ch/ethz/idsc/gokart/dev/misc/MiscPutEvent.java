// code by nisaak and jph
package ch.ethz.idsc.gokart.dev.misc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** misc information sent to micro-autobox */
public class MiscPutEvent extends DataEvent {
  private static final int LENGTH = 6;
  private static final byte _1 = 1;
  private static final byte _0 = 0;
  // ---
  static final MiscPutEvent FALLBACK = new MiscPutEvent(_0, _0, _0, _0, _0, _0);
  static final MiscPutEvent RESETCON = new MiscPutEvent(_1, _0, _0, _0, _0, _0);
  // ---
  /** table of values for resetConnection:
   * 0 - for normal operation
   * 1 - to acknowledge communication timeout */
  public final byte resetConnection;
  public final byte resetRimoL;
  public final byte resetRimoR;
  public final byte resetLinmot;
  public final byte resetSteer;
  public final byte ledControl;

  public MiscPutEvent(byte resetConnection, byte resetRimoL, byte resetRimoR, byte resetLinmot, byte resetSteer, byte ledControl) {
    this.resetConnection = resetConnection;
    this.resetRimoL = resetRimoL;
    this.resetRimoR = resetRimoR;
    this.resetLinmot = resetLinmot;
    this.resetSteer = resetSteer;
    this.ledControl = ledControl;
  }

  public MiscPutEvent(ByteBuffer byteBuffer) {
    this(byteBuffer.get(), byteBuffer.get(), byteBuffer.get(), byteBuffer.get(), byteBuffer.get(), byteBuffer.get());
  }

  @Override // from DataEvent
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(resetConnection);
    byteBuffer.put(resetRimoL);
    byteBuffer.put(resetRimoR);
    byteBuffer.put(resetLinmot);
    byteBuffer.put(resetSteer);
    byteBuffer.put(ledControl);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector( //
        resetConnection & 0xff, //
        resetRimoL & 0xff, //
        resetRimoR & 0xff, //
        resetLinmot & 0xff, //
        resetSteer & 0xff, //
        ledControl & 0xff //
    );
  }
}
