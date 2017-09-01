// code by jph
package ch.ethz.idsc.retina.dev.lidar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface LidarRayBlockListener {
  /** @param floatBuffer
   * @param byteBuffer */
  // TODO probably should provide time info
  void digest(FloatBuffer floatBuffer, ByteBuffer byteBuffer);
}
