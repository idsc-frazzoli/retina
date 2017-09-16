// code by jph
package ch.ethz.idsc.retina.dev.lidar.urg04lx;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.lidar.LidarRayDataProvider;
import ch.ethz.idsc.retina.util.GlobalAssert;

public class Urg04lxDecoder implements LidarRayDataProvider {
  /** 'U' 'B' as short in little endian
   * "BU" == Binary Urg */
  private static final short HEADER = 0x4255;
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
