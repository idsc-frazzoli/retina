// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;

public class RimoPutEvent {
  /** 4 bytes encoding length */
  public static final int LENGTH = 4;
  /** according to tests on the bench, the max effective speed is ~6300 */
  public static final short MAX_SPEED = 6500;
  // ---
  public short command;
  /** speed in rad/min */
  public short speed;

  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putShort(command);
    byteBuffer.putShort(speed);
  }
}
