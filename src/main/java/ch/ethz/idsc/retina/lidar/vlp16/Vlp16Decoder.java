// code by jph
package ch.ethz.idsc.retina.lidar.vlp16;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodynePosEvent;
import ch.ethz.idsc.retina.lidar.VelodynePosListener;
import ch.ethz.idsc.retina.lidar.VelodyneStatics;

/** access to a single firing packet containing rotational angle, range,
 * intensity, etc.
 * 
 * implementation based on instructions in:
 * 63-9243 Rev B User Manual and Programming Guide,VLP-16.pdf */
public class Vlp16Decoder implements VelodyneDecoder {
  private static final int FIRINGS = 12;
  private static final byte DUAL = 0x39;
  @SuppressWarnings("unused")
  private static final int FLAG = 61183;
  // ---
  private final List<VelodynePosListener> posListeners = new LinkedList<>();

  @Override
  public void addPosListener(VelodynePosListener listener) {
    posListeners.add(listener);
  }

  @Override
  public boolean hasPosListeners() {
    return !posListeners.isEmpty();
  }

  // ---
  private final List<LidarRayDataListener> rayListeners = new LinkedList<>();

  @Override
  public void addRayListener(LidarRayDataListener listener) {
    rayListeners.add(listener);
  }

  @Override
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
      if (value != 0x22) {
        System.out.println(value);
        GlobalAssert.that(value == 0x22);
      }
      rayListeners.forEach(listener -> listener.timestamp(gps_timestamp, type));
    }
    if (type != DUAL) { // SINGLE 24 blocks of firing data
      // ---
      int az00 = byteBuffer.getShort(offset + 2) & 0xffff;
      int az11 = byteBuffer.getShort(offset + 2 + 1100) & 0xffff;
      // when the sensor operates at 20 revolutions per second, then typically gap == 39
      final int gap = VelodyneStatics.lookupAzimuth(az11 - az00) / 22; // division by 22 == 11 * 2
      // ---
      byteBuffer.position(offset);
      for (int firing = 0; firing < FIRINGS; ++firing) {
        // Each data block begins with a two-byte start identifier
        // 0xFF 0xEE -> 0xEEFF (as short) == 61183
        byteBuffer.getShort(); // two-byte start identifier
        final int azimuth = byteBuffer.getShort() & 0xffff; // rotational [0, ..., 35999]
        // ---
        final int position = byteBuffer.position();
        rayListeners.forEach(listener -> {
          byteBuffer.position(position);
          listener.scan(azimuth, byteBuffer);
        });
        int azimuth_hi = (azimuth + gap) % VelodyneStatics.AZIMUTH_RESOLUTION;
        final int position_hi = position + 48; // 16 * 3
        rayListeners.forEach(listener -> {
          byteBuffer.position(position_hi);
          listener.scan(azimuth_hi, byteBuffer);
        });
        byteBuffer.position(position + 96);
      }
    } else { // DUAL 24 blocks of firing data
      int az00 = byteBuffer.getShort(offset + 2) & 0xffff;
      int az11 = byteBuffer.getShort(offset + 2 + 1100) & 0xffff;
      // when the sensor operates at 20 revolutions per second, then typically gap == 39
      final int gap = VelodyneStatics.lookupAzimuth(az11 - az00) / 10; // division by 10 == 5 * 2
      for (int firing = 0; firing < FIRINGS; firing += 2) {
        final int azimuth;
        {
          byteBuffer.position(offset + firing * 100);
          byteBuffer.getShort(); // two-byte start identifier
          azimuth = byteBuffer.getShort() & 0xffff; // rotational [0, ..., 35999]
          // ---
          final int position = byteBuffer.position();
          rayListeners.forEach(listener -> {
            byteBuffer.position(position);
            listener.scan(azimuth, byteBuffer);
          });
        }
        {
          int position = offset + (firing + 1) * 100 + 4;
          rayListeners.forEach(listener -> {
            byteBuffer.position(position);
            listener.scan(azimuth, byteBuffer);
          });
        }
        final int azimuth_hi = azimuth + gap;
        {
          int position = offset + firing * 100 + 48 + 4;
          rayListeners.forEach(listener -> {
            byteBuffer.position(position);
            listener.scan(azimuth_hi, byteBuffer);
          });
        }
        {
          int position = offset + (firing + 1) * 100 + 48 + 4;
          rayListeners.forEach(listener -> {
            byteBuffer.position(position);
            listener.scan(azimuth_hi, byteBuffer);
          });
        }
      }
    }
  }

  /** @param byteBuffer
   * with at least 512 bytes to read */
  @Override // from VelodyneDecoder
  public void positioning(ByteBuffer byteBuffer) {
    VelodynePosEvent velodynePosEvent = VelodynePosEvent.vlp16(byteBuffer);
    posListeners.forEach(listener -> listener.velodynePos(velodynePosEvent));
  }
}
