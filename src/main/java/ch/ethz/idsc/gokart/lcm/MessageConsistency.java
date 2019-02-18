// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Scalar;

/** passive log listener. corrupt messages lead to a notification in the console */
public enum MessageConsistency implements OfflineLogListener {
  INSTANCE;
  // ---
  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    // System.out.println(time + " " + channel + " " + byteBuffer.remaining());
  }
}
