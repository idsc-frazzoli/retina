// code by jph
package ch.ethz.idsc.retina.dev.lidar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class LidarRayBlockEvent {
  public final FloatBuffer floatBuffer;
  public final ByteBuffer byteBuffer;

  public LidarRayBlockEvent(FloatBuffer floatBuffer, ByteBuffer byteBuffer) {
    this.floatBuffer = floatBuffer;
    this.byteBuffer = byteBuffer;
  }
}
