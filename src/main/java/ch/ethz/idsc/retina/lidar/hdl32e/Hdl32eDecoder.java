// code by jph
package ch.ethz.idsc.retina.lidar.hdl32e;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.owl.data.GlobalAssert;
import ch.ethz.idsc.retina.lidar.LidarRayDataListener;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodynePosListener;

/** information on p.21 of HDL-32E user's manual */
public final class Hdl32eDecoder implements VelodyneDecoder {
  private static final int FIRINGS = 12;
  // ---
  private final List<VelodynePosListener> posListeners = new LinkedList<>();
  private final List<LidarRayDataListener> rayListeners = new LinkedList<>();

  @Override
  public void addPosListener(VelodynePosListener listener) {
    posListeners.add(listener);
  }

  @Override
  public void addRayListener(LidarRayDataListener listener) {
    rayListeners.add(listener);
  }

  @Override
  public boolean hasPosListeners() {
    return !posListeners.isEmpty();
  }

  @Override
  public boolean hasRayListeners() {
    return !rayListeners.isEmpty();
  }

  /** @param byteBuffer with at least 512 bytes to read */
  @Override
  public void positioning(ByteBuffer byteBuffer) {
    byte[] nmea = new byte[72]; // NMEA positioning sentence
    // first 14 bytes not used
    byteBuffer.getLong(); // 8
    byteBuffer.getInt(); // 12
    byteBuffer.getShort(); // 14
    double[] gyro = new double[3];
    double[] temp = new double[3];
    double[] accx = new double[3];
    double[] accy = new double[3];
    for (int index = 0; index < 3; ++index) {
      gyro[index] = StaticHelper.signed24bit(byteBuffer.getShort()) * 0.09766;
      temp[index] = StaticHelper.signed24bit(byteBuffer.getShort()) * 0.1453 + 25;
      accx[index] = StaticHelper.signed24bit(byteBuffer.getShort()) * 0.001221;
      accy[index] = StaticHelper.signed24bit(byteBuffer.getShort()) * 0.001221;
    }
    int pos = byteBuffer.position() + 160;
    byteBuffer.position(pos);
    int gps_usec = byteBuffer.getInt(); // from the hour
    byteBuffer.getInt(); // not used
    byteBuffer.get(nmea);
    Hdl32ePosEvent hdl32ePosEvent = new Hdl32ePosEvent(gps_usec, new String(nmea), gyro, temp, accx, accy);
    posListeners.forEach(listener -> listener.velodynePos(hdl32ePosEvent));
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
      GlobalAssert.that(value == 0x21);
      rayListeners.forEach(listener -> listener.timestamp(gps_timestamp, type));
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
        rayListeners.forEach(listener -> {
          byteBuffer.position(position);
          listener.scan(rotational, byteBuffer);
        });
        byteBuffer.position(position + 96);
      }
    }
  }
}
