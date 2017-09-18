// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;

/** information sent to micro-autobox to control the steering servo */
public class SteerPutEvent {
  public static final int LENGTH = 5;
  // ---
  public byte command;
  // TODO NRJ not finalized, at the moment this is position instead of torque!
  public float torque;

  public SteerPutEvent() {
    // TODO NRJ provide command and torque in constructor and make variables final
  }

  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.put(command);
    byteBuffer.putFloat(torque);
  }
}
