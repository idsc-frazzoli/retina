// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.util.data.DataEvent;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** information sent to micro-autobox to control the steering servo */
public class SteerPutEvent extends DataEvent {
  private static final int LENGTH = 5;
  private static final Word MOT_TRQ_0 = Word.createByte("OFF", (byte) 0);
  private static final Word MOT_TRQ_1 = Word.createByte("ON", (byte) 1);
  /** imaginary unit that encodes angular/rotational position of steer column */
  public static final Unit UNIT_ENCODER = Unit.of("SCE");
  public static final Unit UNIT_RTORQUE = Unit.of("SCT"); // relative torque, not quite N*m but stronger
  public static final ScalarUnaryOperator ENCODER = QuantityMagnitude.singleton(UNIT_ENCODER);
  public static final ScalarUnaryOperator RTORQUE = QuantityMagnitude.singleton(UNIT_RTORQUE);
  public static final List<Word> COMMANDS = Arrays.asList(MOT_TRQ_0, MOT_TRQ_1);
  public static final SteerPutEvent PASSIVE_MOT_TRQ_0 = new SteerPutEvent(SteerPutEvent.MOT_TRQ_0, 0);
  public static final SteerPutEvent PASSIVE_MOT_TRQ_1 = new SteerPutEvent(SteerPutEvent.MOT_TRQ_1, 0);

  /** @param command
   * @param torque with unit "SCT"
   * @return */
  public static final SteerPutEvent create(Word command, Scalar torque) {
    return new SteerPutEvent(command, RTORQUE.apply(torque).number().floatValue());
  }

  /** @param torque with unit "SCT"
   * @return */
  public static final SteerPutEvent createOn(Scalar torque) {
    return create(MOT_TRQ_1, torque);
  }

  /** @param byteBuffer for instance from message in log file
   * @return */
  public static final SteerPutEvent from(ByteBuffer byteBuffer) {
    return new SteerPutEvent(Word.createByte("", byteBuffer.get()), byteBuffer.getFloat());
  }

  // ---
  private final byte command;
  private final float torque;

  /** Remark: the exact valid range of the torque value is not known due to secrecy
   * by the manufacturer of the steering column. Also we don't know the mapping of
   * the torque value to a physical meaningful unit, e.g. N*m.
   * 
   * @param command
   * @param torque typically in the interval [-1.5, +1.5] */
  private SteerPutEvent(Word command, float torque) {
    this.command = command.getByte();
    this.torque = torque;
  }

  @Override // from DataEvent
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(command);
    byteBuffer.putFloat(torque);
  }

  @Override // from DataEvent
  protected int length() {
    return LENGTH;
  }

  /** @return torque with unit "SCT" */
  public Scalar getTorque() {
    return Quantity.of(torque, UNIT_RTORQUE);
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector( //
        command & 0xff, // ... 0
        torque // ............ 1
    );
  }
}
