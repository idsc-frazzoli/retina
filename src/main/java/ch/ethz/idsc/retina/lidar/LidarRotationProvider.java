// code by jph
package ch.ethz.idsc.retina.lidar;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/** converts firing data to spacial events with time, 3d-coordinates and
 * intensity
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class LidarRotationProvider implements LidarRayDataListener {
  /** init value 0 is mandatory for all sensors that transmit complete scan
   * example: urg04lxug01 */
  static final int ROTATIONAL_INIT = 0;
  // ---
  private final List<LidarRotationListener> listeners = new LinkedList<>();
  private int usec;
  private int rotational_last = ROTATIONAL_INIT;

  public void addListener(LidarRotationListener listener) {
    listeners.add(listener);
  }

  @Override
  public void timestamp(int usec, int type) {
    this.usec = usec;
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    if (rotational <= rotational_last) {
      LidarRotationEvent lidarRotationEvent = new LidarRotationEvent(usec, rotational);
      listeners.forEach(listener -> listener.lidarRotation(lidarRotationEvent));
    }
    rotational_last = rotational;
  }
}
