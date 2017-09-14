// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;

public class SteerPutEvent {
  public static final int LENGTH = 5;
  // ---
  public byte command;
  public float torque;

  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(command);
    byteBuffer.putFloat(torque);
  }
}
