// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;
import ch.ethz.idsc.retina.util.data.Word;

/** information sent to micro-autobox to forward to the linear motor that
 * controls the break of the gokart */
public class LinmotPutEvent extends DataEvent {
  /** 12 bytes encoding length */
  private static final int LENGTH = 12;
  // ---
  public final short control_word;
  public final short motion_cmd_hdr;
  public short target_position;
  public short max_velocity;
  public short acceleration;
  public short deceleration;

  public LinmotPutEvent(Word control, Word motion) {
    this.control_word = control.getShort();
    this.motion_cmd_hdr = motion.getShort();
  }

  /** @param byteBuffer
   * with at least 12 bytes remaining */
  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putShort(control_word);
    byteBuffer.putShort(motion_cmd_hdr);
    byteBuffer.putShort(target_position);
    byteBuffer.putShort(max_velocity);
    byteBuffer.putShort(acceleration);
    byteBuffer.putShort(deceleration);
  }

  public String toInfoString() {
    return String.format("%d %d %d %d %d %d", //
        control_word, motion_cmd_hdr, //
        target_position, max_velocity, //
        acceleration, deceleration);
  }

  @Override
  protected int length() {
    return LENGTH;
  }
}
