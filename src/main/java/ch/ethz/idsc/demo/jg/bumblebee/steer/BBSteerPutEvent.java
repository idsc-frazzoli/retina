// code by gjoel
package ch.ethz.idsc.demo.jg.bumblebee.steer;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;

public class BBSteerPutEvent extends DataEvent {
  public static final int LENGTH = 2;
  public static final Unit UNIT_TRQ = Unit.of("N*m");
  public static final Unit UNIT_ANG = Unit.of("deg");
  public static final Unit UNIT_ANG_SPD = Unit.of("rad*sec^-1");

  /* package */ static final double SCALE_TRQ = 0.00048828125;
  /* package */ static final double SCALE_ANG = 0.015735626221;
  /* package */ static final double SCALE_ANG_SPD = 0.045776367188;

  public final static BBSteerPutEvent PASSIVE = new BBSteerPutEvent((short) 0);

  public static BBSteerPutEvent create(Scalar torque) {
    return new BBSteerPutEvent((short) Math.round(QuantityMagnitude.singleton(UNIT_TRQ).apply(torque).number().doubleValue() / SCALE_TRQ));
  }

  public static BBSteerPutEvent from(ByteBuffer byteBuffer) {
    return new BBSteerPutEvent(byteBuffer.getShort());
  }

  // ---
  private final short torque;

  private BBSteerPutEvent(short torque) {
    this.torque = torque;
  }

  @Override // from DataEvent
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putShort(torque);
  }

  @Override // from DataEvent
  public int length() {
    return LENGTH;
  }

  /** @return torque with unit "N*m" */
  public Scalar getTorque() {
    return Quantity.of(SCALE_TRQ * torque, UNIT_TRQ);
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector(torque);
  }
}
