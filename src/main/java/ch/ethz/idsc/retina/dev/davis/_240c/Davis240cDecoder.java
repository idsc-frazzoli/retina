// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis.DavisApsEventListener;
import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisDvsEventListener;
import ch.ethz.idsc.retina.dev.davis.DavisImuEventListener;

/** maps the chip raw dvs/aps data to the standard coordinate system (x,y) where
 * (0,0) corresponds to left-upper corner, and
 * (x,0) parameterizes the first/top row
 * (0,y) parameterizes the first/left column */
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
  private final List<DavisDvsEventListener> dvsDavisEventListeners = new LinkedList<>();
  private final List<DavisApsEventListener> sigDavisEventListeners = new LinkedList<>();
  private final List<DavisApsEventListener> rstDavisEventListeners = new LinkedList<>();
  private final List<DavisImuEventListener> imuDavisEventListeners = new LinkedList<>();

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
      dvsDavisEventListeners.forEach(listener -> listener.dvs(dvsDavisEvent));
    } else {
      final int read = data & 0x0c00;
      if (read == SIGNAL_READ) { // signal read
        final int adc = data & ADC_MAX;
        DavisApsEvent apsDavisEvent = new DavisApsEvent(time, x, LAST_Y - y, ADC_MAX - adc);
        sigDavisEventListeners.forEach(listener -> listener.aps(apsDavisEvent));
      } else //
      if (read == RESET_READ) { // reset read
        final int adc = data & ADC_MAX;
        DavisApsEvent apsDavisEvent = new DavisApsEvent(time, x, LAST_Y - y, ADC_MAX - adc);
        rstDavisEventListeners.forEach(listener -> listener.aps(apsDavisEvent));
      } else //
      if (read == IMU) { // imu
        DavisImuEvent imuDavisEvent = new DavisImuEvent(time, data);
        imuDavisEventListeners.forEach(listener -> listener.imu(imuDavisEvent));
      }
    }
  }

  @Override
  public void addDvsListener(DavisDvsEventListener listener) {
    dvsDavisEventListeners.add(listener);
  }

  @Override
  public void addSigListener(DavisApsEventListener listener) {
    sigDavisEventListeners.add(listener);
  }

  @Override
  public void addRstListener(DavisApsEventListener listener) {
    rstDavisEventListeners.add(listener);
  }

  @Override
  public void addImuListener(DavisImuEventListener listener) {
    imuDavisEventListeners.add(listener);
  }

  public static void main(String[] args) {
    System.out.println(String.format("%08x", ADC_MAX));
  }
}
