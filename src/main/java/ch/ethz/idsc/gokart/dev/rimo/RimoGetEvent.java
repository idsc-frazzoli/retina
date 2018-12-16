// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;

/** Specifications about gokart battery
 * 
 * End-of-charging voltage: 57.12[V]
 * Discharge limit (below which damage may occur): 44.8[V]
 * Capacity: 100[A*h] == draw 100[A] for 1[h]
 * Maximum continuous load: 3.5[C] => 350 A
 * Peak load: 10[C] => 1[kA] for less than 10[s] */
public class RimoGetEvent extends DataEvent {
  /* package */ static final int LENGTH = 2 * RimoGetTire.LENGTH; // == 48
  // ---
  public final RimoGetTire getTireL;
  public final RimoGetTire getTireR;

  /** @param byteBuffer with {@link ByteOrder#LITTLE_ENDIAN} */
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

  /** @return vector with 2 entries: left and right rear wheel angular rate in unit "rad*s^-1" */
  public Tensor getAngularRate_Y_pair() {
    return Tensors.of( //
        getTireL.getAngularRate_Y(), //
        getTireR.getAngularRate_Y());
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Join.of( //
        getTireL.asVector(), //
        getTireR.asVector());
  }
}
