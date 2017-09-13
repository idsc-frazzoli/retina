// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.nio.ByteBuffer;

public class RimoPutEvent {
  public short left_command;
  public short left_speed;
  public short right_command;
  public short right_speed;

  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putShort(left_command);
    byteBuffer.putShort(left_speed);
    byteBuffer.putShort(right_command);
    byteBuffer.putShort(right_speed);
  }
}
