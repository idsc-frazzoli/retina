// code by jph
package ch.ethz.idsc.retina.lidar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

/** collects a lidar scan of a single packet */
public class LidarPacketCollector implements LidarSpacialListener, LidarRayDataListener {
  private final List<LidarRayBlockListener> listeners = new LinkedList<>();
  private final int limit;
  private final int dimensions;
  private final FloatBuffer floatBuffer;
  private final ByteBuffer byteBuffer;

  /** the highway scene has 2304 * 32 * 3 == 221184 coordinates
   * 
   * @param limit
   * @param dimensions */
  public LidarPacketCollector(int limit, int dimensions) {
    this.limit = limit;
    this.dimensions = dimensions;
    this.floatBuffer = FloatBuffer.wrap(new float[limit * dimensions]);
    this.byteBuffer = ByteBuffer.wrap(new byte[limit]);
  }

  public void addListener(LidarRayBlockListener listener) {
    listeners.add(listener);
  }

  @Override // from LidarSpacialListener
  public void lidarSpacial(LidarXYZEvent lidarXYZEvent) {
    floatBuffer.put(lidarXYZEvent.coords); // either 3, or 2 floats
    byteBuffer.put(lidarXYZEvent.intensity);
  }

  @Override
  public void timestamp(int usec, int type) {
    // set limit of buffers to current position
    floatBuffer.flip();
    byteBuffer.flip();
    LidarRayBlockEvent lidarRayBlockEvent = //
        new LidarRayBlockEvent(usec, floatBuffer, byteBuffer, dimensions);
    listeners.forEach(listener -> listener.lidarRayBlock(lidarRayBlockEvent));
    // ---
    // reset buffers
    floatBuffer.limit(limit * dimensions);
    floatBuffer.position(0);
    byteBuffer.limit(limit);
    byteBuffer.position(0);
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    // TODO JPH design not elegant
  }
}
