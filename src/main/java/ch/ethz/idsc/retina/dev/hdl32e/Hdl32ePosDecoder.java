// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.math.ShortUtils;

/** information on p.21 of HDL-32E user's manual */
public final class Hdl32ePosDecoder {
  private final List<Hdl32ePosEventListener> listeners = new LinkedList<>();

  public void addListener(Hdl32ePosEventListener listener) {
    listeners.add(listener);
  }

  public boolean hasListeners() {
    return !listeners.isEmpty();
  }

  /** @param byteBuffer with at least 512 bytes to read */
  public void positioning(ByteBuffer byteBuffer) {
    byte[] nmea = new byte[72]; // NMEA positioning sentence
    Hdl32ePosEvent hdl32ePosEvent = new Hdl32ePosEvent();
    // first 14 bytes not used
    byteBuffer.getLong(); // 8
    byteBuffer.getInt(); // 12
    byteBuffer.getShort(); // 14
    for (int index = 0; index < 3; ++index) {
      hdl32ePosEvent.gyro[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.09766;
      hdl32ePosEvent.temp[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.1453 + 25;
      hdl32ePosEvent.accx[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.001221;
      hdl32ePosEvent.accy[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.001221;
    }
    int pos = byteBuffer.position() + 160;
    byteBuffer.position(pos);
    hdl32ePosEvent.gps_usec = byteBuffer.getInt(); // from the hour
    byteBuffer.getInt(); // not used
    byteBuffer.get(nmea);
    hdl32ePosEvent.nmea = new String(nmea);
    listeners.forEach(listener -> listener.positioning(hdl32ePosEvent));
  }
}
