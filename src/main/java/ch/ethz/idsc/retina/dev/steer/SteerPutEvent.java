// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.retina.util.data.Word;

/** information sent to micro-autobox to control the steering servo */
public class SteerPutEvent extends DataEvent {
  public static final int LENGTH = 5;
  public static final Word CMD_OFF = Word.createByte("OFF", (byte) 0);
  public static final Word CMD_ON = Word.createByte("ON", (byte) 1);
  public static final List<Word> COMMANDS = Arrays.asList( //
      CMD_OFF, CMD_ON);

  public static final SteerPutEvent from(ByteBuffer byteBuffer) {
    return new SteerPutEvent(Word.createByte("", byteBuffer.get()), byteBuffer.getFloat());
  }

  // ---
  private final byte command;
  private final float torque;
  public static final double MAX_ANGLE = 0.6743167638778687;

  /** @param command
   * @param torque TODO NRJ determine valid range */
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
