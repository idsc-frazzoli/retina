// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;

/** for post processing @see RimoPutHelper */
public class RimoPutEvent extends DataEvent {
  /* package */ static final int LENGTH = 2 * RimoPutTire.LENGTH;
  /** instance of command to apply zero torque to the rear wheels */
  public static final RimoPutEvent PASSIVE = //
      new RimoPutEvent(RimoPutTire.PASSIVE, RimoPutTire.PASSIVE);
  // ---
  public final RimoPutTire putTireL;
  public final RimoPutTire putTireR;

  public RimoPutEvent(RimoPutTire putTireL, RimoPutTire putTireR) {
    this.putTireL = putTireL;
    this.putTireR = putTireR;
  }

  @Override // from DataEvent
  public void insert(ByteBuffer byteBuffer) {
    putTireL.insert(byteBuffer);
    putTireR.insert(byteBuffer);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  /** @return torque of left and right motor in unit "ARMS" with sign convention around Y-axis */
  public Tensor getTorque_Y_pair() {
    return Tensors.of( //
        putTireL.getTorque().negate(), //
        putTireR.getTorque());
  }

  @Override
  public Tensor asVector() {
    return Join.of( //
        putTireL.asVector(), //
        putTireR.asVector());
  }
}
