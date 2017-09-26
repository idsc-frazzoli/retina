// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.util.data.Word;

public class RimoPutTire implements Serializable {
  public static final Word OPERATION = Word.createShort("OPERATION", (short) 0x0009);
  public static final List<Word> COMMANDS = Arrays.asList(OPERATION);
  public static final RimoPutTire STOP = new RimoPutTire(OPERATION, (short) 0);

  public static RimoPutTire withSpeed(short speed) {
    return new RimoPutTire(OPERATION, speed);
  }

  // ---
  /** 4 bytes encoding length */
  /* package */ static final int LENGTH = 4;
  /** according to tests on the bench, the max effective speed is ~6300 */
  public static final short MAX_SPEED = 6500;
  // ---
  final short command;
  /** speed in rad/min */
  final short speed;

  public RimoPutTire(Word command, short speed) {
    this.command = command.getShort();
    this.speed = speed;
  }

  public short getSpeedRaw() {
    return speed;
  }

  void insert(ByteBuffer byteBuffer) {
    byteBuffer.putShort(command);
    byteBuffer.putShort(speed);
  }
}
