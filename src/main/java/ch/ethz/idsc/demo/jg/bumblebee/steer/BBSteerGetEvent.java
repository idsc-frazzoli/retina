// code by gjoel
package ch.ethz.idsc.demo.jg.bumblebee.steer;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public class BBSteerGetEvent extends DataEvent {
  /* package */ static final int LENGTH = 8;

  public final short angSpd;
  public final short tsuTrq;
  public final short refMotTrq;
  public final short estMotTrq;

  public BBSteerGetEvent(ByteBuffer byteBuffer) {
    angSpd = byteBuffer.getShort();
    tsuTrq = byteBuffer.getShort();
    refMotTrq = byteBuffer.getShort();
    estMotTrq = byteBuffer.getShort();
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putShort(angSpd);
    byteBuffer.putShort(tsuTrq);
    byteBuffer.putShort(refMotTrq);
    byteBuffer.putShort(estMotTrq);
  }

  @Override // from BufferInsertable
  public int length() {
    return LENGTH;
  }

  @Override
  public Tensor asVector() {
    return Tensors.vector( //
        angSpd, //
        tsuTrq, //
        refMotTrq, //
        estMotTrq //
    );
  }

  /** @return motor angle speed with unit "rad*sec^-1" */
  public Scalar angSpd() {
    return Quantity.of(BBSteerPutEvent.SCALE_ANG_SPD * angSpd, BBSteerPutEvent.UNIT_ANG_SPD);
  }

  /** @return column torque sensor signal with unit "N*m" */
  public Scalar tsuTrq() {
    return Quantity.of(BBSteerPutEvent.SCALE_TRQ * tsuTrq, BBSteerPutEvent.UNIT_TRQ);
  }

  /** @return reference motor torque with unit "N*m" */
  public Scalar refMotTrq() {
    return Quantity.of(BBSteerPutEvent.SCALE_TRQ * refMotTrq, BBSteerPutEvent.UNIT_TRQ);
  }

  /** @return estimated motor torque with unit "N*m" */
  public Scalar estMotTrq() {
    return Quantity.of(BBSteerPutEvent.SCALE_TRQ * estMotTrq, BBSteerPutEvent.UNIT_TRQ);
  }

  // /** @return relative integral motor angle with unit "deg" */
  // public Scalar relIntRotAng() {
  //   return Quantity.of(BBSteerPutEvent.SCALE_ANG * relIntRotAng, BBSteerPutEvent.UNIT_ANG);
  // }
}
