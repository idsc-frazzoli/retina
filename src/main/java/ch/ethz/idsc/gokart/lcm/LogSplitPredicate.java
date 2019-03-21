// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Scalar;

public interface LogSplitPredicate {
  /** @param time
   * @param channel
   * @param byteBuffer
   * @return true if function call triggers a new log segment */
  boolean split(Scalar time, String channel, ByteBuffer byteBuffer);
}
