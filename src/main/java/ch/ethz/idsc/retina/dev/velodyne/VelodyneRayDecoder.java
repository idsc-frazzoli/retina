// code by jph
package ch.ethz.idsc.retina.dev.velodyne;

import java.nio.ByteBuffer;

public interface VelodyneRayDecoder {
  void lasers(ByteBuffer byteBuffer);
}
