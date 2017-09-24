// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;

public class RimoGetEvent extends DataEvent {
  public static final int LENGTH = 2 * RimoGetTire.LENGTH;
  // ---
  public final RimoGetTire getL;
  public final RimoGetTire getR;

  public RimoGetEvent(RimoGetTire getL, RimoGetTire getR) {
    this.getL = getL;
    this.getR = getR;
  }

  @Override
  protected void insert(ByteBuffer byteBuffer) {
    getL.encode(byteBuffer);
    getR.encode(byteBuffer);
  }

  @Override
  protected int length() {
    return LENGTH;
  }
}
