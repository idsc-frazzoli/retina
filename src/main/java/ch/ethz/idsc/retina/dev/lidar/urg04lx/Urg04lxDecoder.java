// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.lidar.LidarRayDataProvider;

public class Urg04lxDecoder implements LidarRayDataProvider {
  private static final int ROTATION = 0;
  // ---
  private final List<LidarRayDataListener> listeners = new LinkedList<>();

  @Override
  public void addRayListener(LidarRayDataListener listener) {
    listeners.add(listener);
  }

  @Override
  public boolean hasRayListeners() {
    return !listeners.isEmpty();
  }

  public void lasers(ByteBuffer byteBuffer) {
    byteBuffer.getShort(); // header
    long timestamp = byteBuffer.getLong(); // 8 byte
    listeners.forEach(listeners -> listeners.timestamp((int) timestamp, 0));
    // ---
    listeners.forEach(listeners -> {
      byteBuffer.position(2 + 8);
      listeners.scan(ROTATION, byteBuffer);
    });
  }
}
