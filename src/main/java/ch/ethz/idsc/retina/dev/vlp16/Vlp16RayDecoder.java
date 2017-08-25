// code by jph
package ch.ethz.idsc.retina.dev.vlp16;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/** access to a single firing packet containing
 * rotational angle, range, intensity, etc. */
public class Vlp16RayDecoder {
  private static final int FIRINGS = 12;
  // ---
  private final List<Vlp16RayDataListener> listeners = new LinkedList<>();

  public void addListener(Vlp16RayDataListener listener) {
    listeners.add(listener);
  }

  public boolean hasListeners() {
    return !listeners.isEmpty();
  }

  /** @param byteBuffer with at least 1206 bytes to read */
  public void lasers(ByteBuffer byteBuffer) {
    final int offset = byteBuffer.position();
    { // status data
      byteBuffer.position(offset + 1200);
      int gps_timestamp = byteBuffer.getInt(); // in [usec]
      byte type = byteBuffer.get(); // 55
      byte value = byteBuffer.get(); // 33
      listeners.forEach(listener -> listener.timestamp(gps_timestamp, type, value));
    }
    { // 12 blocks of firing data
      byteBuffer.position(offset);
      for (int firing = 0; firing < FIRINGS; ++firing) {
        @SuppressWarnings("unused")
        int blockId = byteBuffer.getShort() & 0xffff; // laser block ID, 61183 ?
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
