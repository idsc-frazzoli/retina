// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.util.LinkedList;
import java.util.List;

import idsc.DavisImu;

/** translates davis imu lcm message to {@link DavisImuFrame}
 * which is then distributed to listeners */
public class DavisImuLcmDecoder {
  private final List<DavisImuFrameListener> listeners = new LinkedList<>();

  public void addListener(DavisImuFrameListener davisImuFrameListener) {
    listeners.add(davisImuFrameListener);
  }

  public void decode(DavisImu davisImu) {
    DavisImuFrame davisImuFrame = new DavisImuFrame(davisImu.clock_usec, davisImu.accel, davisImu.temperature, davisImu.gyro);
    listeners.forEach(listener -> listener.imuFrame(davisImuFrame));
  }
}
