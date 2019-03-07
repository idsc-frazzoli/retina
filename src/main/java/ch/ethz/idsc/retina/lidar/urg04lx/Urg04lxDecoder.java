// code by jph
package ch.ethz.idsc.retina.lidar.urg04lx;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.lidar.LidarRayDataProvider;

public class Urg04lxDecoder implements LidarRayDataProvider {
  /** 'U' 'B' as short in little endian "BU" == Binary Urg */
  private static final short HEADER = 0x4255;
  private static final int ROTATION = 0;
  // ---
  private final List<LidarRayDataListener> listeners = new CopyOnWriteArrayList<>();

  @Override
  public void addRayListener(LidarRayDataListener listener) {
    listeners.add(listener);
  }

  public void removeRayListener(LidarRayDataListener listener) {
    listeners.remove(listener);
  }

  @Override
  public boolean hasRayListeners() {
    return !listeners.isEmpty();
  }

  public void lasers(ByteBuffer byteBuffer) {
    // header 1st byte == 'U', 2nd byte == 'B'
    short header = byteBuffer.getShort();
    GlobalAssert.that(header == HEADER);
    long timestamp = byteBuffer.getLong(); // 8 byte
    listeners.forEach(listeners -> listeners.timestamp((int) timestamp, 0));
    // ---
    listeners.forEach(listeners -> {
      byteBuffer.position(2 + 8);
      listeners.scan(ROTATION, byteBuffer);
    });
  }
}
