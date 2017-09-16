// code by jph
package ch.ethz.idsc.retina.dev.lidar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class LidarRayBlockEvent {
  // TODO still have to agree on a universal time-stamp
  public final int usec;
  public final FloatBuffer floatBuffer;
  public final ByteBuffer byteBuffer;

  public LidarRayBlockEvent(int usec, FloatBuffer floatBuffer, ByteBuffer byteBuffer) {
    this.usec = usec;
    this.floatBuffer = floatBuffer;
    this.byteBuffer = byteBuffer;
  }
}
