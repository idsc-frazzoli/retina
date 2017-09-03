// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;

public class Urg04lxDecoder {
  private static final int ROTATION = 0;
  // ---
  private final List<LidarRayDataListener> listeners = new LinkedList<>();

  public void addListener(LidarRayDataListener listener) {
    listeners.add(listener);
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
