package ch.ethz.idsc.retina.dev.misc;

import java.nio.ByteBuffer;

public class MiscPutEvent {
  public static final int LENGTH = 5;
  // ---
  public byte resetRimoL;
  public byte resetRimoR;
  public byte resetLinmot;
  public byte resetSteer;
  public byte ledControl;

  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(resetRimoL);
    byteBuffer.put(resetRimoR);
    byteBuffer.put(resetLinmot);
    byteBuffer.put(resetSteer);
    byteBuffer.put(ledControl);
  }
}
