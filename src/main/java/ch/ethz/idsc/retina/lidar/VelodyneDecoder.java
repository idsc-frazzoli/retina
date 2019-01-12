// code by jph
package ch.ethz.idsc.retina.lidar;

import java.nio.ByteBuffer;

/** data handling for hdl32e and vlp16 sensors */
public interface VelodyneDecoder extends LidarRayDataProvider {
  void positioning(ByteBuffer byteBuffer);

  void lasers(ByteBuffer byteBuffer);

  void addPosListener(VelodynePosListener listener);

  boolean hasPosListeners();
}
