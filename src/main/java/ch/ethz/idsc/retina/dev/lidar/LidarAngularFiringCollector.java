// code by jph
package ch.ethz.idsc.retina.dev.lidar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.GlobalAssert;

/** collects a complete 360 rotation
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class LidarAngularFiringCollector implements LidarSpacialEventListener, LidarRotationEventListener {
  /** the highway scene has 2304 * 32 * 3 == 221184 coordinates */
  // TODO parts of the implementation are not generic
  private static final int MAX_COORDINATES = 2304 * 32; // == 221184

  @Deprecated
  public static LidarAngularFiringCollector createDefault() {
    FloatBuffer floatBuffer = FloatBuffer.wrap(new float[MAX_COORDINATES * 3]); // 3 because of x y z
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[MAX_COORDINATES]);
    return new LidarAngularFiringCollector(floatBuffer, byteBuffer);
  }

  // ---
  private final FloatBuffer floatBuffer;
  private final ByteBuffer byteBuffer;
  private final int limit;
  private final List<LidarRayBlockListener> listeners = new LinkedList<>();

  public LidarAngularFiringCollector(FloatBuffer floatBuffer, ByteBuffer byteBuffer) {
    this.floatBuffer = floatBuffer;
    this.byteBuffer = byteBuffer;
    limit = byteBuffer.limit();
    GlobalAssert.that(floatBuffer.limit() == limit * 3);
  }

  public void addListener(LidarRayBlockListener listener) {
    listeners.add(listener);
  }

  @Override
  public void rotation(LidarRotationEvent lidarRotationEvent) {
    floatBuffer.flip();
    byteBuffer.flip();
    listeners.forEach(listener -> listener.digest(floatBuffer, byteBuffer));
    floatBuffer.limit(limit * 3);
    floatBuffer.position(0);
    byteBuffer.limit(limit);
    byteBuffer.position(0);
  }

  @Override
  public void spacial(LidarSpacialEvent lidarSpacialEvent) {
    floatBuffer.put(lidarSpacialEvent.coords);
    byteBuffer.put((byte) lidarSpacialEvent.intensity);
  }
}
