// code by jph
package ch.ethz.idsc.retina.dev.hdl32e.data;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.hdl32e.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.hdl32e.LidarRotationEvent;
import ch.ethz.idsc.retina.dev.hdl32e.LidarRotationEventListener;

/** converts firing data to spacial events with time, 3d-coordinates and intensity */
// TODO OWLY3D uses class
public class LidarRotationProvider implements LidarRayDataListener {
  private final List<LidarRotationEventListener> listeners = new LinkedList<>();
  private int usec;
  private int rotational_last = -1;

  public void addListener(LidarRotationEventListener listener) {
    listeners.add(listener);
  }

  @Override
  public void timestamp(int usec, byte type, byte value) {
    this.usec = usec;
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    if (rotational < rotational_last) {
      LidarRotationEvent lidarRotationEvent = new LidarRotationEvent(usec, rotational);
      listeners.forEach(listener -> listener.rotation(lidarRotationEvent));
    }
    rotational_last = rotational;
  }
}
