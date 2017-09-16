// code by jph
package ch.ethz.idsc.retina.dev.lidar.hdl32e;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEventListener;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneStatics;

/** extracts points at horizontal level */
public class Hdl32ePlanarEmulator implements LidarSpacialProvider {
  private static final double ANGLE_FACTOR = Math.PI / 18000.0;
  // ---
  private final List<LidarSpacialEventListener> listeners = new LinkedList<>();
  /* package for testing */ int limit_lo = 10; // TODO choose reasonable value
  private int usec;

  @Override
  public void addListener(LidarSpacialEventListener lidarSpacialEventListener) {
    listeners.add(lidarSpacialEventListener);
  }

  /** quote from the user's manual, p.8:
   * "the minimum return distance for the HDL-32E is approximately 1 meter.
   * ignore returns closer than this"
   * 
   * however, we find that in office conditions correct ranges
   * below 1 meter are provided
   * 
   * @param closest in [m] */
  public void setLimitLo(double closest) {
    limit_lo = (int) (closest / VelodyneStatics.TO_METER);
  }

  @Override
  public void timestamp(int usec, int type) {
    this.usec = usec;
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    final double angle = rotational * ANGLE_FACTOR;
    final float dx = (float) Math.cos(angle);
    final float dy = (float) -Math.sin(angle);
    final float[] coords = new float[2];
    int laser = 15;
    byteBuffer.position(byteBuffer.position() + laser * 3);
    int distance = byteBuffer.getShort() & 0xffff;
    int intensity = byteBuffer.get() & 0xff;
    if (limit_lo <= distance) {
      // "report distance to the nearest 0.2 cm" => 2 mm
      float range = distance * VelodyneStatics.TO_METER_FLOAT; // convert to [m]
      coords[0] = range * dx;
      coords[1] = range * dy;
      LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, intensity);
      listeners.forEach(listener -> listener.spacial(lidarSpacialEvent));
    }
  }
}
