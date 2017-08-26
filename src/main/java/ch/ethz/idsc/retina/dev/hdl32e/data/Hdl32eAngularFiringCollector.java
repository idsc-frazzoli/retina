// code by jph
package ch.ethz.idsc.retina.dev.hdl32e.data;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eRayBlockListener;
import ch.ethz.idsc.retina.dev.hdl32e.LidarRotationEvent;
import ch.ethz.idsc.retina.dev.hdl32e.LidarRotationEventListener;
import ch.ethz.idsc.retina.dev.hdl32e.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.hdl32e.LidarSpacialEventListener;
import ch.ethz.idsc.retina.util.GlobalAssert;

/** collects a complete 360 rotation */
// TODO OWLY3D uses class
public class Hdl32eAngularFiringCollector implements LidarSpacialEventListener, LidarRotationEventListener {
  /** the highway scene has 2304 * 32 * 3 == 221184 coordinates */
  public static final int MAX_COORDINATES = 2304 * 32 * 3; // == 221184
  // ---
  private final FloatBuffer floatBuffer;
  private final ByteBuffer byteBuffer;
  private final int limit;
  private final List<Hdl32eRayBlockListener> listeners = new LinkedList<>();

  public Hdl32eAngularFiringCollector(FloatBuffer floatBuffer, ByteBuffer byteBuffer) {
    this.floatBuffer = floatBuffer;
    this.byteBuffer = byteBuffer;
    limit = byteBuffer.limit();
    GlobalAssert.that(floatBuffer.limit() == limit * 3);
  }

  public void addListener(Hdl32eRayBlockListener listener) {
    listeners.add(listener);
  }

  @Override
  public void rotation(LidarRotationEvent hdl32eRotationEvent) {
    floatBuffer.flip();
    byteBuffer.flip();
    listeners.forEach(listener -> listener.digest(floatBuffer, byteBuffer));
    floatBuffer.limit(limit * 3);
    floatBuffer.position(0);
    byteBuffer.limit(limit);
    byteBuffer.position(0);
  }

  @Override
  public void spacial(LidarSpacialEvent hdl32eSpacialEvent) {
    floatBuffer.put(hdl32eSpacialEvent.coords);
    byteBuffer.put((byte) hdl32eSpacialEvent.intensity);
  }
}
