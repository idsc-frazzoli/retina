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
    long timestamp = byteBuffer.getLong();
    listeners.forEach(listeners -> listeners.timestamp((int) timestamp, (byte) 0));
    // ---
    listeners.forEach(listeners -> {
      byteBuffer.position(0);
      listeners.scan(ROTATION, byteBuffer);
    });
  }
}
