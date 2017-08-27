// code by jph
package ch.ethz.idsc.retina.dev.velodyne;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/** converts firing data to spacial events with time, 3d-coordinates and intensity
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class LidarRotationProvider implements LidarRayDataListener {
  private final List<LidarRotationEventListener> listeners = new LinkedList<>();
  private int usec;
  private int rotational_last = -1;

  public void addListener(LidarRotationEventListener listener) {
    listeners.add(listener);
  }

  @Override
  public void timestamp(int usec, byte type) {
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
