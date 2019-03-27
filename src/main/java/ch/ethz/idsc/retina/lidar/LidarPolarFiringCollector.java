// code by jph, gjoel
package ch.ethz.idsc.retina.lidar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.owl.data.GlobalAssert;

// TODO could be integrated in LidarAngulrFiringCollector
/** collects a lidar scan of a complete 360 rotation into a pointcloud consisting
 * of 3d points */
public class LidarPolarFiringCollector implements LidarPolarListener, LidarRotationListener {
  private final FloatBuffer floatBuffer;
  private final ByteBuffer byteBuffer;
  private final int limit;
  private final List<LidarRayBlockListener> listeners = new LinkedList<>();
  private final int dimensions;

  /** the highway scene has 2304 * 32 * 3 == 221184 coordinates
   *
   * @param limit
   * @param dimensions */
  public LidarPolarFiringCollector(int limit, int dimensions) {
    this.floatBuffer = FloatBuffer.wrap(new float[limit * dimensions]); // 2 because of x y;
    this.byteBuffer = ByteBuffer.wrap(new byte[limit]);
    this.limit = limit;
    this.dimensions = dimensions;
    GlobalAssert.that(floatBuffer.limit() == limit * dimensions);
  }

  public void addListener(LidarRayBlockListener listener) {
    listeners.add(listener);
  }

  @Override // from LidarRotationListener
  public void lidarRotation(LidarRotationEvent lidarRotationEvent) {
    // set limit of buffers to current position
    floatBuffer.flip();
    byteBuffer.flip();
    LidarRayBlockEvent lidarRayBlockEvent = //
        new LidarRayBlockEvent(lidarRotationEvent.usec, floatBuffer, byteBuffer, dimensions);
    listeners.forEach(listener -> listener.lidarRayBlock(lidarRayBlockEvent));
    // ---
    // reset buffers
    floatBuffer.limit(limit * dimensions);
    floatBuffer.position(0);
    byteBuffer.limit(limit);
    byteBuffer.position(0);
  }

  @Override // from LidarPolarListener
  public void lidarSpacial(LidarPolarEvent lidarPolarEvent) {
    floatBuffer.put(lidarPolarEvent.coords);
    byteBuffer.put(lidarPolarEvent.intensity);
  }
}
