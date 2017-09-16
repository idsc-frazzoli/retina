// code by jph
package ch.ethz.idsc.retina.dev.lidar;

import java.awt.Point;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.GlobalAssert;

/** collects a lidar scan of a complete 360 rotation
 * into a {@link Point} of 3d, or 2d
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class LidarAngularFiringCollector implements LidarSpacialEventListener, LidarRotationEventListener {
  /** the highway scene has 2304 * 32 * 3 == 221184 coordinates */
  public static LidarAngularFiringCollector create3d(int max) {
    FloatBuffer floatBuffer = FloatBuffer.wrap(new float[max * 3]); // 3 because of x y z
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[max]);
    return new LidarAngularFiringCollector(floatBuffer, byteBuffer);
  }

  public static LidarAngularFiringCollector create2d(int max) {
    FloatBuffer floatBuffer = FloatBuffer.wrap(new float[max * 2]); // 2 because of x y
    ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[max]);
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
    // set limit of buffers to current position
    floatBuffer.flip();
    byteBuffer.flip();
    LidarRayBlockEvent lidarRayBlockEvent = new LidarRayBlockEvent(lidarRotationEvent.usec, floatBuffer, byteBuffer);
    listeners.forEach(listener -> listener.lidarRayBlock(lidarRayBlockEvent));
    // ---
    // reset buffers
    floatBuffer.limit(limit * 3);
    floatBuffer.position(0);
    byteBuffer.limit(limit);
    byteBuffer.position(0);
  }

  @Override
  public void spacial(LidarSpacialEvent lidarSpacialEvent) {
    floatBuffer.put(lidarSpacialEvent.coords); // either 3, or 2 floats
    byteBuffer.put((byte) lidarSpacialEvent.intensity);
  }
}
