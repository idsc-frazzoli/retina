// code by jph
package ch.ethz.idsc.retina.dev.lidar;

import java.nio.ByteBuffer;

/** data handling for hdl32e and vlp16 sensors */
public interface VelodyneDecoder {
  void positioning(ByteBuffer byteBuffer);

  void lasers(ByteBuffer byteBuffer);

  void addPosListener(VelodynePosEventListener listener);

  void addRayListener(LidarRayDataListener listener);
}
