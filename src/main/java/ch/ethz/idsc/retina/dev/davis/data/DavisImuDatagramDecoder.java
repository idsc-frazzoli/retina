// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class DavisImuDatagramDecoder {
  private final List<DavisImuFrameListener> listeners = new LinkedList<>();

  public void addListener(DavisImuFrameListener davisImuEventListener) {
    listeners.add(davisImuEventListener);
  }

  private short pacid_next = -1;
  private int missed;
  private int missed_print;
  private long total;

  public void decode(ByteBuffer byteBuffer) {
    byteBuffer.position(0);
    int time = byteBuffer.getInt();
    short pacid = byteBuffer.getShort(); // running id of packet
    // if (pacid_next != pacid)
    // System.err.println("imu packet missing " + pacid_next);
    float[] value = new float[7];
    for (int index = 0; index < 7; ++index)
      value[index] = byteBuffer.getFloat();
    DavisImuFrame davisImuFrame = new DavisImuFrame(time, value);
    listeners.forEach(listener -> listener.imuFrame(davisImuFrame));
  }
}
