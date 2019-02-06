// code by jph
package ch.ethz.idsc.gokart.offline.channel;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Tensor;

public interface SingleChannelInterface {
  /** @return channel name */
  String channel();

  /** @param byteBuffer
   * @return vector of message to be appended to table */
  Tensor row(ByteBuffer byteBuffer);
}
