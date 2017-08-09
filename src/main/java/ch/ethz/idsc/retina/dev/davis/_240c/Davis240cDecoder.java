// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis.ApsDavisEventListener;
import ch.ethz.idsc.retina.dev.davis.DavisDecoder;
import ch.ethz.idsc.retina.dev.davis.DavisEventListener;
import ch.ethz.idsc.retina.dev.davis.DvsDavisEventListener;
import ch.ethz.idsc.retina.dev.davis.ImuDavisEventListener;

class Davis240cDecoder implements DavisDecoder {
  private static final int WIDTH = 240;
  private static final int HEIGHT = 180;
  private static final int LAST_X = WIDTH - 1;
  private static final int LAST_Y = HEIGHT - 1;
  private static final int ADC_MAX = 1023;
  // ---
  private final List<DvsDavisEventListener> dvsDavisEventListeners = new LinkedList<>();
  private final List<ApsDavisEventListener> apsDavisEventListeners = new LinkedList<>();
  private final List<ImuDavisEventListener> imuDavisEventListeners = new LinkedList<>();

  @Override
  public DvsDavisEvent encodeDvs(int time, int x, int y, int i) {
    return new DvsDavisEvent(time, LAST_X - x, LAST_Y - y, i);
  }

  @Override
  public ApsDavisEvent encodeAps(int time, int x, int y, int adc) {
    return new ApsDavisEvent(time, x, LAST_Y - y, ADC_MAX - adc);
  }

  @Override
  public void read(ByteBuffer byteBuffer) {
    final int data = byteBuffer.getInt(); // also referred to "address"
    final int time = byteBuffer.getInt(); // microseconds
    final int x = (data >> 12) & 0x3ff; // length 10 bit
    final int y = (data >> 22) & 0x1ff; // length 09 bit
    final boolean isDvs = (data & 0x80000000) == 0;
    if (isDvs) {
      final int i = (data >> 11) & 1; // length 1 bit
      DvsDavisEvent dvsDavisEvent = encodeDvs(time, x, y, i);
      dvsDavisEventListeners.forEach(listener -> listener.dvs(dvsDavisEvent));
    } else {
      final int read = (data >> 10) & 0x3;
      if (read == 1) { // signal
        final int adc = data & 0x3ff;
        ApsDavisEvent apsDavisEvent = encodeAps(time, x, y, adc);
        apsDavisEventListeners.forEach(listener -> listener.aps(apsDavisEvent));
      } else //
      if (read == 0) { // reset read
      } else //
      if (read == 3) { // imu
        ImuDavisEvent imuDavisEvent = new ImuDavisEvent(time, data);
        imuDavisEventListeners.forEach(listener -> listener.imu(imuDavisEvent));
      }
    }
  }

  @Override
  public void addListener(DavisEventListener davisEventListener) {
    if (davisEventListener instanceof DvsDavisEventListener)
      dvsDavisEventListeners.add((DvsDavisEventListener) davisEventListener);
    if (davisEventListener instanceof ApsDavisEventListener)
      apsDavisEventListeners.add((ApsDavisEventListener) davisEventListener);
    if (davisEventListener instanceof ImuDavisEventListener)
      imuDavisEventListeners.add((ImuDavisEventListener) davisEventListener);
  }

  @Override
  public ByteOrder getByteOrder() {
    return ByteOrder.BIG_ENDIAN;
  }
}
