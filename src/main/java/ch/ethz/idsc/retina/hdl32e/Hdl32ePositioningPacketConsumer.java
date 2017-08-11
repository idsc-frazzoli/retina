// code by jph
package ch.ethz.idsc.retina.hdl32e;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.util.math.ShortUtils;

/** information on p.21 of HDL-32E user's manual */
public final class Hdl32ePositioningPacketConsumer {
  private final List<Hdl32ePositioningListener> listeners = new LinkedList<>();

  public void addListener(Hdl32ePositioningListener hdl32ePositioningListener) {
    listeners.add(hdl32ePositioningListener);
  }

  /** @param byteBuffer with at least 512 bytes to read */
  public void positioning(ByteBuffer byteBuffer) {
    byte[] nmea = new byte[72]; // NMEA positioning sentence
    Hdl32ePositioningEvent hdl32eImuEvent = new Hdl32ePositioningEvent();
    // first 14 bytes not used
    byteBuffer.getLong(); // 8
    byteBuffer.getInt(); // 12
    byteBuffer.getShort(); // 14
    for (int index = 0; index < 3; ++index) {
      hdl32eImuEvent.gyro[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.09766;
      hdl32eImuEvent.temp[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.1453 + 25;
      hdl32eImuEvent.accx[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.001221;
      hdl32eImuEvent.accy[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.001221;
    }
    int pos = byteBuffer.position() + 160;
    byteBuffer.position(pos);
    hdl32eImuEvent.gps_usec = byteBuffer.getInt(); // from the hour
    byteBuffer.getInt(); // not used
    byteBuffer.get(nmea);
    hdl32eImuEvent.nmea = new String(nmea);
    listeners.forEach(listener -> listener.positioning(hdl32eImuEvent));
  }
}
