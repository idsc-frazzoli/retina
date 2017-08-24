// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface Hdl32eFiringBlockListener {
  /** @param floatBuffer
   * @param byteBuffer */
  void digest(FloatBuffer floatBuffer, ByteBuffer byteBuffer);
}
