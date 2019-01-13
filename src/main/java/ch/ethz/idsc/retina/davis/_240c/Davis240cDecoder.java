// code by jph
package ch.ethz.idsc.retina.davis._240c;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.davis.DavisApsListener;
import ch.ethz.idsc.retina.davis.DavisDecoder;
import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis.DavisImuListener;

/** maps the chip raw dvs/aps data to the standard coordinate system (x,y) where
 * (0,0) corresponds to left-upper corner, and (x,0) parameterizes the first/top
 * row (0,y) parameterizes the first/left column */
public class Davis240cDecoder implements DavisDecoder {
  private static final int WIDTH = 240;
  private static final int HEIGHT = 180;
  private static final int LAST_X = WIDTH - 1;
  private static final int LAST_Y = HEIGHT - 1;
  private static final int ADC_MAX = 0x03ff;
  private static final int SIGNAL_READ = 0x0400;
  private static final int RESET_READ = 0;
  private static final int IMU = 0x0c00;
  // ---
  private final List<DavisDvsListener> dvsDavisEventListeners = new LinkedList<>();
  private final List<DavisApsListener> sigDavisEventListeners = new LinkedList<>();
  private final List<DavisApsListener> rstDavisEventListeners = new LinkedList<>();
  private final List<DavisImuListener> imuDavisEventListeners = new LinkedList<>();

  @Override
  public void read(ByteBuffer byteBuffer) { // BIG_ENDIAN
    int data = byteBuffer.getInt(); // also referred to "address"
    int time = byteBuffer.getInt(); // microseconds
    read(data, time);
  }

  @Override
  public void read(int data, int time) {
    final int x = (data >> 12) & 0x3ff; // length 10 bit
    final int y = (data >> 22) & 0x1ff; // length 09 bit
    final boolean isDvs = (data & 0x80000000) == 0;
    if (isDvs) {
      final int i = (data >> 11) & 1; // length 1 bit
      DavisDvsEvent dvsDavisEvent = new DavisDvsEvent(time, LAST_X - x, LAST_Y - y, i);
      dvsDavisEventListeners.forEach(listener -> listener.davisDvs(dvsDavisEvent));
    } else {
      final int read = data & 0x0c00;
      if (read == SIGNAL_READ) { // signal read
        final int adc = data & ADC_MAX;
        DavisApsEvent apsDavisEvent = new DavisApsEvent(time, x, LAST_Y - y, ADC_MAX - adc);
        sigDavisEventListeners.forEach(listener -> listener.davisAps(apsDavisEvent));
      } else //
      if (read == RESET_READ) { // reset read
        final int adc = data & ADC_MAX;
        DavisApsEvent apsDavisEvent = new DavisApsEvent(time, x, LAST_Y - y, ADC_MAX - adc);
        rstDavisEventListeners.forEach(listener -> listener.davisAps(apsDavisEvent));
      } else //
      if (read == IMU) { // imu
        DavisImuEvent davisImuEvent = new DavisImuEvent(time, data);
        imuDavisEventListeners.forEach(listener -> listener.davisImu(davisImuEvent));
      }
    }
  }

  @Override
  public void addDvsListener(DavisDvsListener listener) {
    dvsDavisEventListeners.add(listener);
  }

  @Override
  public void addSigListener(DavisApsListener listener) {
    sigDavisEventListeners.add(listener);
  }

  @Override
  public void addRstListener(DavisApsListener listener) {
    rstDavisEventListeners.add(listener);
  }

  @Override
  public void addImuListener(DavisImuListener listener) {
    imuDavisEventListeners.add(listener);
  }
}
