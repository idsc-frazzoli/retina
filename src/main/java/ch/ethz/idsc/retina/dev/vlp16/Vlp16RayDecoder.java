// code by jph
package ch.ethz.idsc.retina.dev.vlp16;

import java.nio.ByteBuffer;

/** access to a single firing packet containing
 * rotational angle, range, intensity, etc. */
public class Vlp16RayDecoder extends ListenerQueue<Vlp16RayDataListener> {
  private static final int FIRINGS = 12;
  // ---
  private final AzimuthExtrapolation ae = new AzimuthExtrapolation();

  /** @param byteBuffer with at least 1206 bytes to read */
  public void lasers(ByteBuffer byteBuffer) {
    final int offset = byteBuffer.position(); // 0 or 42
    { // status data
      byteBuffer.position(offset + 1200);
      int gps_timestamp = byteBuffer.getInt(); // in [usec]
      // System.out.println(gps_timestamp);
      // 55 == 0x37 == Strongest return
      // 56 == 0x38 == Last return
      // 57 == 0x39 == Dual return
      byte type = byteBuffer.get();
      @SuppressWarnings("unused")
      byte value = byteBuffer.get(); // 34 == 0x22 == VLP-16
      // TODO assert
      listeners.forEach(listener -> listener.timestamp(gps_timestamp, type));
    }
    { // 12 blocks of firing data
      byteBuffer.position(offset);
      for (int firing = 0; firing < FIRINGS; ++firing) {
        // 0xFF 0xEE -> 0xEEFF (as short) == 61183
        @SuppressWarnings("unused")
        int flag = byteBuffer.getShort() & 0xffff; // laser block ID, 61183 ?
        final int azimuth = byteBuffer.getShort() & 0xffff; // rotational [0, ..., 35999]
        ae.now(azimuth);
        // System.out.println(azimuth);
        // ---
        final int position = byteBuffer.position();
        listeners.forEach(listener -> {
          byteBuffer.position(position);
          listener.scan(azimuth, byteBuffer);
        });
        int azimuth_hi = ae.gap();
        final int position_hi = position + 48; // 16*3
        listeners.forEach(listener -> {
          byteBuffer.position(position_hi);
          listener.scan(azimuth_hi, byteBuffer);
        });
        byteBuffer.position(position + 96);
      }
    }
  }
}
