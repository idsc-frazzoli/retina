// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.gokart.core.DataEvent;
import ch.ethz.idsc.retina.sys.OfflineUse;
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
  /** imaginary unit that encodes angular/rotational position of steer column */
  public static final Unit UNIT_ENCODER = Unit.of("SCE");
  public static final Unit UNIT_RTORQUE = Unit.of("SCT"); // relative torque, not quite N*m but stronger
  public static final ScalarUnaryOperator RTORQUE = QuantityMagnitude.singleton(UNIT_RTORQUE);
  public static final ScalarUnaryOperator ENCODER = QuantityMagnitude.singleton(UNIT_ENCODER);
  public static final Word CMD_OFF = Word.createByte("OFF", (byte) 0);
  public static final Word CMD_ON = Word.createByte("ON", (byte) 1);
  public static final List<Word> COMMANDS = Arrays.asList(CMD_OFF, CMD_ON);
  public static final SteerPutEvent PASSIVE = new SteerPutEvent(SteerPutEvent.CMD_OFF, 0);

  /** @param command
   * @param torque with unit "SCT"
   * @return */
  public static final SteerPutEvent create(Word command, Scalar torque) {
    return new SteerPutEvent(command, RTORQUE.apply(torque).number().floatValue());
  }

  /** @param torque with unit "SCT"
   * @return */
  public static final SteerPutEvent createOn(Scalar torque) {
    return create(CMD_ON, torque);
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

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(command);
    byteBuffer.putFloat(torque);
  }

  @Override
  protected int length() {
    return LENGTH;
  }

  public Scalar getTorque() {
    return Quantity.of(torque, UNIT_RTORQUE);
  }

  /** @return vector of length 2 */
  @OfflineUse
  public Tensor values_raw() {
    return Tensors.vector(command & 0xff, torque);
  }
}
