// code by jph
package ch.ethz.idsc.retina.dev.velodyne.hdl32e;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

// TODO OWLY3D uses class
// TODO class is generic for vlp16
public interface Hdl32eRayBlockListener {
  /** @param floatBuffer
   * @param byteBuffer */
  // TODO probably should provide time info
  void digest(FloatBuffer floatBuffer, ByteBuffer byteBuffer);
}
