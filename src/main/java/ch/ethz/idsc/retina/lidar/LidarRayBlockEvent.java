// code by jph
package ch.ethz.idsc.retina.lidar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class LidarRayBlockEvent {
  // LONGTERM still have to agree on/confirm a universal time-stamp
  public final int usec;
  public final FloatBuffer floatBuffer;
  public final ByteBuffer byteBuffer;
  /** dimensions == 3 if float buffer consists of x,y,z
   * dimensions == 2 if float buffer consists of x,y */
  public final int dimensions;

  public LidarRayBlockEvent(int usec, FloatBuffer floatBuffer, ByteBuffer byteBuffer, int dimensions) {
    this.usec = usec;
    this.floatBuffer = floatBuffer;
    this.byteBuffer = byteBuffer;
    this.dimensions = dimensions;
  }

  /** @return number of points stored in block */
  public int size() {
    return byteBuffer.limit();
  }
}
