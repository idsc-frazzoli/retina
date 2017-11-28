// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class RimoGetEvent extends DataEvent {
  /* package */ static final int LENGTH = 2 * RimoGetTire.LENGTH;
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

  /** @return vector with 2 entries: left and right rear wheel angular rate */
  public Tensor getAngularRate_Y_pair() {
    return Tensors.of( //
        getTireL.getAngularRate_Y(), //
        getTireR.getAngularRate_Y());
  }
}
