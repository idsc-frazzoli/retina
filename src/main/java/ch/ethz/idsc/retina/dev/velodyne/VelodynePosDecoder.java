// code by jph
package ch.ethz.idsc.retina.dev.velodyne;

import java.nio.ByteBuffer;

public interface VelodynePosDecoder {
  void positioning(ByteBuffer byteBuffer);
}
