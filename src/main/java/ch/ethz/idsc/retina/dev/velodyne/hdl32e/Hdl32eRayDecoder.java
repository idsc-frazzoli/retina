// code by jph
package ch.ethz.idsc.retina.dev.velodyne.hdl32e;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.velodyne.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.velodyne.VelodyneRayDecoder;

/** access to a single firing packet containing
 * rotational angle, range, intensity, etc. */
public class Hdl32eRayDecoder implements VelodyneRayDecoder {
  private static final int FIRINGS = 12;
  // ---
  private final List<LidarRayDataListener> listeners = new LinkedList<>();

  public void addListener(LidarRayDataListener listener) {
    listeners.add(listener);
  }

  public boolean hasListeners() {
    return !listeners.isEmpty();
  }

  /** @param byteBuffer with at least 1206 bytes to read */
  @Override
  public void lasers(ByteBuffer byteBuffer) {
    final int offset = byteBuffer.position();
    { // status data
      byteBuffer.position(offset + 1200);
      int gps_timestamp = byteBuffer.getInt(); // in [usec]
      byte type = byteBuffer.get(); // 55 == 0x37 == Strongest return
      byte value = byteBuffer.get(); // 33 == 0x21 == HDL-32E
      listeners.forEach(listener -> listener.timestamp(gps_timestamp, type));
    }
    { // 12 blocks of firing data
      byteBuffer.position(offset);
      for (int firing = 0; firing < FIRINGS; ++firing) {
        // 0xFF 0xEE -> 0xEEFF (as short) == 61183
        @SuppressWarnings("unused")
        int flag = byteBuffer.getShort() & 0xffff;
        int rotational = byteBuffer.getShort() & 0xffff; // rotational [0, ..., 35999]
        // ---
        final int position = byteBuffer.position();
        listeners.forEach(listener -> {
          byteBuffer.position(position);
          listener.scan(rotational, byteBuffer);
        });
        byteBuffer.position(position + 96);
      }
    }
  }
}
