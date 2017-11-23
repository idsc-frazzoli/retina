// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;

public class RimoGetEvent extends DataEvent {
  public static final int LENGTH = 2 * RimoGetTire.LENGTH;
  // ---
  public final RimoGetTire getTireL;
  public final RimoGetTire getTireR;

  public RimoGetEvent(ByteBuffer byteBuffer) {
    getTireL = new RimoGetTire(byteBuffer, -1);
    getTireR = new RimoGetTire(byteBuffer, +1);
  }

  @Override
  protected void insert(ByteBuffer byteBuffer) {
    getTireL.encode(byteBuffer);
    getTireR.encode(byteBuffer);
  }

  @Override
  protected int length() {
    return LENGTH;
  }
}
