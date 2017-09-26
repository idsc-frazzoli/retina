// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class RimoPutTire implements Serializable {
  public static final Word OPERATION = Word.createShort("OPERATION", (short) 0x0009);
  public static final List<Word> COMMANDS = Arrays.asList(OPERATION);
  public static final RimoPutTire STOP = new RimoPutTire(OPERATION, (short) 0);
  public static final double MIN_TO_S = 1 / 60.0;

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
  /** angular rate in rad/min */
  final short rate;

  public RimoPutTire(Word command, short rate) {
    this.command = command.getShort();
    this.rate = rate;
  }

  /** only for use in display
   * 
   * @return */
  public short getRateRaw() {
    return rate;
  }

  /** @return convert rad/min to rad/s */
  public Scalar getAngularRate() {
    return Quantity.of(rate * MIN_TO_S, "s^-1");
  }

  void insert(ByteBuffer byteBuffer) {
    byteBuffer.putShort(command);
    byteBuffer.putShort(rate);
  }
}
