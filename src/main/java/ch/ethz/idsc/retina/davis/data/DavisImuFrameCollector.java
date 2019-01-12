// code by jph
package ch.ethz.idsc.retina.davis.data;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.davis.DavisImuListener;
import ch.ethz.idsc.retina.davis.DavisStatics;
import ch.ethz.idsc.retina.davis._240c.DavisImuEvent;

/** the conversion formulas are trimmed to match the values in the jAER demo
 * therefore the absolute values should be correct the ordering */
public class DavisImuFrameCollector implements DavisImuListener {
  private final ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[DavisImuFrame.LENGTH]);
  private final List<DavisImuFrameListener> listeners = new LinkedList<>();

  public DavisImuFrameCollector() {
    byteBuffer.order(DavisStatics.BYTE_ORDER);
  }

  public void addListener(DavisImuFrameListener davisImuFrameListener) {
    listeners.add(davisImuFrameListener);
  }

  @Override
  public void davisImu(DavisImuEvent davisImuEvent) {
    final int ordinal = davisImuEvent.index;
    byteBuffer.putShort(4 + 2 * ordinal, davisImuEvent.value);
    if (ordinal == 6) {
      byteBuffer.putInt(0, davisImuEvent.time);
      byteBuffer.position(0);
      DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
      listeners.forEach(listener -> listener.imuFrame(davisImuFrame));
    }
  }
}
