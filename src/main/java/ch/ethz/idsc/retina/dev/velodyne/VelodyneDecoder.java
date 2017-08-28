// code by jph
package ch.ethz.idsc.retina.dev.velodyne;

import java.nio.ByteBuffer;

public interface VelodyneDecoder {
  void positioning(ByteBuffer byteBuffer);

  void lasers(ByteBuffer byteBuffer);

  void addPosListener(VelodynePosEventListener listener);

  void addRayListener(LidarRayDataListener listener);
}
