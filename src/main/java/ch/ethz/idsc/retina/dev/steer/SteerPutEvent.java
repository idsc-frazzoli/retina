// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.util.data.Word;

/** information sent to micro-autobox to control the steering servo */
public class SteerPutEvent implements Serializable {
  public static final int LENGTH = 5;
  public static final Word CMD_OFF = Word.createByte("OFF", (byte) 0);
  public static final Word CMD_ON = Word.createByte("ON", (byte) 1);
  public static final List<Word> COMMANDS = Arrays.asList( //
      CMD_OFF, CMD_ON);
  // ---
  public final byte command;
  // TODO NRJ not finalized, at the moment this is position instead of torque!
  public final float torque;

  public SteerPutEvent(byte command, float torque) {
    this.command = command;
    this.torque = torque;
  }

  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(command);
    byteBuffer.putFloat(torque);
  }
}
