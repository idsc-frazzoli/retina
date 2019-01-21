// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Scalar;

@FunctionalInterface
public interface OfflineLogListener {
  /** function processes message from log file
   * 
   * @param time with time unit
   * @param channel
   * @param byteBuffer */
  void event(Scalar time, String channel, ByteBuffer byteBuffer);
}
