// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.io.Serializable;
import java.nio.ByteBuffer;

/** information sent to micro-autobox to control the steering servo */
public class SteerPutEvent implements Serializable {
  public static final int LENGTH = 5;
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
