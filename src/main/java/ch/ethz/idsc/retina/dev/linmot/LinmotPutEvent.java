// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.nio.ByteBuffer;

public class LinmotPutEvent {
  /** 12 bytes encoding length */
  public static final int LENGTH = 12;
  // ---
  public short control_word;
  public short motion_cmd_hdr;
  public short target_position;
  public short max_velocity;
  public short acceleration;
  public short deceleration;

  /** @param byteBuffer with at least 12 bytes remaining */
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
}
