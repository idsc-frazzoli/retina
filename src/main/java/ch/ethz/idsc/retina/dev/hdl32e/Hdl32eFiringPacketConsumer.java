// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;

/** access to a single firing packet containing
 * rotational angle, range, intensity, etc. */
public interface Hdl32eFiringPacketConsumer {
  /** @param byteBuffer with at least 1206 bytes to read */
  void lasers(ByteBuffer byteBuffer);
}
