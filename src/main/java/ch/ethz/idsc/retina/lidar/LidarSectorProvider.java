// code by jph, gjoel
package ch.ethz.idsc.retina.lidar;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

/** converts firing data to spacial events with time, 3d-coordinates and
 * intensity */
public class LidarSectorProvider implements LidarRayDataListener {
  /** init value 0 is mandatory for all sensors that transmit complete scan
   * example: urg04lxug01 */
  static final int ROTATIONAL_INIT = 0;
  // ---
  private final int sectorWidth;
  private final List<LidarRotationListener> listeners = new LinkedList<>();
  private int usec;
  private int rotational_last = ROTATIONAL_INIT;

  /** @param azimuthResolution steps per one full 360° rotation
   * @param sectors to split one full 360° rotation in */
  public LidarSectorProvider(int azimuthResolution, int sectors) {
    int sec = sectors;
    while (azimuthResolution % sec != 0)
      ++sec;
    System.out.println("sector width = " + (360. / sec) + "°");
    sectorWidth = azimuthResolution / sec;
  }

  public void addListener(LidarRotationListener listener) {
    listeners.add(listener);
  }

  @Override
  public void timestamp(int usec, int type) {
    this.usec = usec;
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    int rot = rotational % sectorWidth;
    if (rot <= rotational_last) {
      LidarRotationEvent lidarRotationEvent = new LidarRotationEvent(usec, rotational);
      listeners.forEach(listener -> listener.lidarRotation(lidarRotationEvent));
    }
    rotational_last = rot;
  }
}
