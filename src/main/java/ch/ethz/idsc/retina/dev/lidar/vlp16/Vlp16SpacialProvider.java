// code by jph
package ch.ethz.idsc.retina.dev.lidar.vlp16;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialEventListener;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;

/** converts firing data to spacial events with time, 3d-coordinates and intensity */
public class Vlp16SpacialProvider implements LidarSpacialProvider {
  private static final int LASERS = 16;
  public static final float[] IR = new float[LASERS];
  public static final float[] IZ = new float[LASERS];
  public static final double ANGLE_FACTOR = Math.PI / 18000.0;
  public static final double TO_METER = 0.002;
  public static final float TO_METER_FLOAT = (float) TO_METER;
  // ---
  private final List<LidarSpacialEventListener> listeners = new LinkedList<>();
  /* package for testing */ int limit_lo = 10; // TODO magic const
  private int usec;

  public Vlp16SpacialProvider() {
    for (int laser = 0; laser < LASERS; ++laser) {
      double theta = degree(laser) * Math.PI / 180;
      IR[laser] = (float) Math.cos(theta);
      IZ[laser] = (float) Math.sin(theta);
    }
  }

  /** quote from the user's manual, p.8:
   * "the minimum return distance for the HDL-32E is approximately 1 meter.
   * ignore returns closer than this"
   * 
   * @param closest in [m] */
  public void setLimitLo(double closest) {
    limit_lo = (int) (closest / TO_METER);
  }

  @Override
  public void addListener(LidarSpacialEventListener lidarSpacialEventListener) {
    listeners.add(lidarSpacialEventListener);
  }

  @Override
  public void timestamp(int usec, int type) {
    this.usec = usec;
  }

  @Override
  public void scan(int azimuth, ByteBuffer byteBuffer) {
    // TODO cos/sin can be done in a lookup table!
    final double angle = azimuth * ANGLE_FACTOR;
    float dx = (float) Math.cos(angle);
    float dy = (float) -Math.sin(angle);
    float[] coords = new float[3];
    for (int laser = 0; laser < LASERS; ++laser) {
      int distance = byteBuffer.getShort() & 0xffff;
      int intensity = byteBuffer.get() & 0xff;
      if (limit_lo <= distance) {
        // "report distance to the nearest 0.2 cm" => 2 mm
        float range = distance * TO_METER_FLOAT; // convert to [m]
        coords[0] = IR[laser] * range * dx;
        coords[1] = IR[laser] * range * dy;
        coords[2] = IZ[laser] * range;
        LidarSpacialEvent lidarSpacialEvent = new LidarSpacialEvent(usec, coords, intensity);
        listeners.forEach(listener -> listener.spacial(lidarSpacialEvent));
      }
    }
  }

  /* package */ static int degree(int laserId) {
    if (laserId < 0)
      throw new RuntimeException();
    if (laserId == 15)
      return 15;
    return -15 + laserId * 16 % 30;
  }
}
