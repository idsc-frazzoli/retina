// code by jph
package ch.ethz.idsc.retina.dev.lidar.vlp16;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.dev.lidar.VelodynePosEventListener;
import ch.ethz.idsc.retina.util.GlobalAssert;

/** access to a single firing packet containing
 * rotational angle, range, intensity, etc. */
public class Vlp16Decoder implements VelodyneDecoder {
  private static final int FIRINGS = 12;
  private static final byte DUAL = 0x39;
  // ---
  private final AzimuthExtrapolation ae = new AzimuthExtrapolation();
  private final List<VelodynePosEventListener> posListeners = new LinkedList<>();

  @Override
  public void addPosListener(VelodynePosEventListener listener) {
    posListeners.add(listener);
  }

  public boolean hasPosListeners() {
    return !posListeners.isEmpty();
  }

  // ---
  private final List<LidarRayDataListener> rayListeners = new LinkedList<>();

  @Override
  public void addRayListener(LidarRayDataListener listener) {
    rayListeners.add(listener);
  }

  public boolean hasRayListeners() {
    return !rayListeners.isEmpty();
  }

  /** @param byteBuffer with at least 1206 bytes to read */
  @Override
  public void lasers(ByteBuffer byteBuffer) {
    final int offset = byteBuffer.position(); // 0 or 42
    final byte type;
    { // status data
      byteBuffer.position(offset + 1200);
      int gps_timestamp = byteBuffer.getInt(); // in [usec]
      // System.out.println(gps_timestamp);
      // 55 == 0x37 == Strongest return
      // 56 == 0x38 == Last return
      // 57 == 0x39 == Dual return
      type = byteBuffer.get();
      byte value = byteBuffer.get(); // 34 == 0x22 == VLP-16
      GlobalAssert.that(value == 0x22);
      rayListeners.forEach(listener -> listener.timestamp(gps_timestamp, type));
    }
    if (type != DUAL) { // SINGLE 24 blocks of firing data
      byteBuffer.position(offset);
      for (int firing = 0; firing < FIRINGS; ++firing) {
        // 0xFF 0xEE -> 0xEEFF (as short) == 61183
        @SuppressWarnings("unused")
        int flag = byteBuffer.getShort() & 0xffff; // laser block ID, 61183 ?
        final int azimuth = byteBuffer.getShort() & 0xffff; // rotational [0, ..., 35999]
        ae.now(azimuth);
        // ---
        final int position = byteBuffer.position();
        rayListeners.forEach(listener -> {
          byteBuffer.position(position);
          listener.scan(azimuth, byteBuffer);
        });
        int azimuth_hi = ae.gap();
        final int position_hi = position + 48; // 16*3
        rayListeners.forEach(listener -> {
          byteBuffer.position(position_hi);
          listener.scan(azimuth_hi, byteBuffer);
        });
        byteBuffer.position(position + 96);
      }
    } else { // DUAL 24 blocks of firing data
      for (int firing = 0; firing < FIRINGS; firing += 2) {
        {
          byteBuffer.position(offset + firing * 100);
          @SuppressWarnings("unused")
          int flag = byteBuffer.getShort() & 0xffff;
          final int azimuth = byteBuffer.getShort() & 0xffff; // rotational [0, ..., 35999]
          ae.now(azimuth);
          // ---
          final int position = byteBuffer.position();
          rayListeners.forEach(listener -> {
            byteBuffer.position(position);
            listener.scan(azimuth, byteBuffer);
          });
        }
        {
          byteBuffer.position(offset + (firing + 1) * 100);
          @SuppressWarnings("unused")
          int flag = byteBuffer.getShort() & 0xffff;
          final int azimuth = byteBuffer.getShort() & 0xffff; // rotational [0, ..., 35999]
          ae.now(azimuth); // TODO should be obsolete since azimuth is the same as before
          // ---
          final int position = byteBuffer.position();
          rayListeners.forEach(listener -> {
            byteBuffer.position(position);
            listener.scan(azimuth, byteBuffer);
          });
        }
        {
          byteBuffer.position(offset + firing * 100 + 48 + 4);
          int azimuth_hi = ae.gap();
          final int position = byteBuffer.position();
          rayListeners.forEach(listener -> {
            byteBuffer.position(position);
            listener.scan(azimuth_hi, byteBuffer);
          });
        }
        {
          byteBuffer.position(offset + (firing + 1) * 100 + 48 + 4);
          int azimuth_hi = ae.gap();
          final int position = byteBuffer.position();
          rayListeners.forEach(listener -> {
            byteBuffer.position(position);
            listener.scan(azimuth_hi, byteBuffer);
          });
        }
      }
    }
  }

  /** @param byteBuffer with at least 512 bytes to read */
  @Override
  public void positioning(ByteBuffer byteBuffer) {
    final int offset = byteBuffer.position(); // 0 or 42 in pcap file
    byteBuffer.position(offset + 198); // unused
    int gps_usec = byteBuffer.getInt(); // TODO from the hour?
    byteBuffer.getInt(); // unused
    byte[] nmea = new byte[72]; // NMEA positioning sentence
    byteBuffer.get(nmea);
    VelodynePosEvent vlp16PosEvent = new VelodynePosEvent(gps_usec, new String(nmea));
    // System.out.println(vlp16PosEvent.gps_usec + " " + vlp16PosEvent.nmea);
    posListeners.forEach(listener -> listener.positioning(vlp16PosEvent));
  }
}
