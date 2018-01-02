// code by jph
package ch.ethz.idsc.retina.dev.davis.data;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

// TODO OUTDATED? check if still needed
public class DavisImuDatagramDecoder {
  private final List<DavisImuFrameListener> listeners = new LinkedList<>();

  public void addListener(DavisImuFrameListener davisImuFrameListener) {
    listeners.add(davisImuFrameListener);
  }

  public void decode(ByteBuffer byteBuffer) {
    byteBuffer.position(0);
    DavisImuFrame davisImuFrame = new DavisImuFrame(byteBuffer);
    listeners.forEach(listener -> listener.imuFrame(davisImuFrame));
  }
}
