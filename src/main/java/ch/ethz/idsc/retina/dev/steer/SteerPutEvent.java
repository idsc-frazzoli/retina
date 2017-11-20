// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** information sent to micro-autobox to control the steering servo */
public class SteerPutEvent extends DataEvent {
  /** imaginary unit that encodes angular/rotational position of steer column */
  public static final Unit UNIT_ENCODER = Unit.of("SCE");
  public static final Unit UNIT_TORQUE = Unit.of("N*m");
  public static final ScalarUnaryOperator NEWTON_METER = QuantityMagnitude.singleton(UNIT_TORQUE);
  public static final ScalarUnaryOperator ENCODER = QuantityMagnitude.singleton(UNIT_ENCODER);
  public static final int LENGTH = 5;
  public static final Word CMD_OFF = Word.createByte("OFF", (byte) 0);
  public static final Word CMD_ON = Word.createByte("ON", (byte) 1);
  public static final List<Word> COMMANDS = Arrays.asList( //
      CMD_OFF, CMD_ON);

  public static final SteerPutEvent from(ByteBuffer byteBuffer) {
    return new SteerPutEvent(Word.createByte("", byteBuffer.get()), byteBuffer.getFloat());
  }

  public static final SteerPutEvent createOn(Scalar torque) {
    return new SteerPutEvent(CMD_ON, NEWTON_METER.apply(torque).number().doubleValue());
  }

  // ---
  private final byte command;
  private final float torque;

  /** @param command
   * @param torque TODO NRJ determine valid range */
  // TODO this could be private in the future
  public SteerPutEvent(Word command, double torque) {
    this.command = command.getByte();
    this.torque = (float) torque;
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(command);
    byteBuffer.putFloat(torque);
  }

  public float getTorque() {
    return torque;
  }

  @Override
  protected int length() {
    return LENGTH;
  }
}
