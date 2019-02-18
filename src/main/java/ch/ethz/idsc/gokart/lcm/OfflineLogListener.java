// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Scalar;

@FunctionalInterface
public interface OfflineLogListener {
  /** function processes message from log file
   *
   * @param utime absolute timestamp of event in micro-seconds since 1970-01-01
   * @param time since begin of log with unit [s]
   * @param channel
   * @param byteBuffer */
  void event(long utime, Scalar time, String channel, ByteBuffer byteBuffer);
}
