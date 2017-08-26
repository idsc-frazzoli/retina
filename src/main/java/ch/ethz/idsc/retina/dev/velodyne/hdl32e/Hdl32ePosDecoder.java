// code by jph
package ch.ethz.idsc.retina.dev.velodyne.hdl32e;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.velodyne.VelodynePosEventListener;
import ch.ethz.idsc.retina.util.math.ShortUtils;

/** information on p.21 of HDL-32E user's manual */
public final class Hdl32ePosDecoder {
  private final List<VelodynePosEventListener> listeners = new LinkedList<>();

  public void addListener(VelodynePosEventListener listener) {
    listeners.add(listener);
  }

  public boolean hasListeners() {
    return !listeners.isEmpty();
  }

  /** @param byteBuffer with at least 512 bytes to read */
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
      gyro[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.09766;
      temp[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.1453 + 25;
      accx[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.001221;
      accy[index] = ShortUtils.signed24bit(byteBuffer.getShort()) * 0.001221;
    }
    int pos = byteBuffer.position() + 160;
    byteBuffer.position(pos);
    int gps_usec = byteBuffer.getInt(); // from the hour
    byteBuffer.getInt(); // not used
    byteBuffer.get(nmea);
    Hdl32ePosEvent hdl32ePosEvent = new Hdl32ePosEvent(gps_usec, new String(nmea), gyro, temp, accx, accy);
    listeners.forEach(listener -> listener.positioning(hdl32ePosEvent));
  }
}
