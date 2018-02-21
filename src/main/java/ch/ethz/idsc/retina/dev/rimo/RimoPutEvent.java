// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.DataEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** for post processing @see RimoPutHelper */
public class RimoPutEvent extends DataEvent {
  /* package */ static final int LENGTH = 2 * RimoPutTire.LENGTH;
  /** instance of command to apply zero torque to the rear wheels */
  public static final RimoPutEvent PASSIVE = //
      new RimoPutEvent(RimoPutTire.PASSIVE, RimoPutTire.PASSIVE);
  // ---
  public final RimoPutTire putL;
  public final RimoPutTire putR;

  public RimoPutEvent(RimoPutTire putL, RimoPutTire putR) {
    this.putL = putL;
    this.putR = putR;
  }

  @Override // from DataEvent
  public void insert(ByteBuffer byteBuffer) {
    putL.insert(byteBuffer);
    putR.insert(byteBuffer);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  /** @return torque of left and right motor in unit "ARMS" with sign convention around Y-axis */
  public Tensor getTorque_Y_pair() {
    return Tensors.of( //
        putL.getTorque().negate(), //
        putR.getTorque());
  }
}
