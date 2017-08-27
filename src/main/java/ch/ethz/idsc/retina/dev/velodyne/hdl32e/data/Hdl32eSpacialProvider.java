// code by jph
package ch.ethz.idsc.retina.dev.velodyne.hdl32e.data;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.velodyne.LidarRayDataListener;
import ch.ethz.idsc.retina.dev.velodyne.LidarSpacialEvent;
import ch.ethz.idsc.retina.dev.velodyne.LidarSpacialEventListener;

/** converts firing data to spacial events with time, 3d-coordinates and intensity
 * 
 * CLASS IS USED OUTSIDE OF PROJECT - MODIFY ONLY IF ABSOLUTELY NECESSARY */
public class Hdl32eSpacialProvider implements LidarRayDataListener {
  private static final int LASERS = 32;
  public static final float[] IR = new float[32];
  public static final float[] IZ = new float[32];
  public static final double ANGLE_FACTOR = 2 * Math.PI / 36000.0;
  public static final double TO_METER = 0.002;
  public static final float TO_METER_FLOAT = (float) TO_METER;
  /** quote from the user's manual, p.12:
   * "the interleaving firing pattern is designed to avoid
   * potential ghosting caused primarily by retro-reflection" */
  public static final int[] ORDERING = new int[] { //
      -23, -7, //
      -22, -6, //
      -21, -5, //
      -20, -4, //
      -19, -3, //
      -18, -2, //
      -17, -1, //
      -16, +0, //
      -15, +1, //
      -14, +2, //
      -13, +3, //
      -12, +4, //
      -11, +5, //
      -10, +6, //
      -9, +7, //
      -8, +8 };
  // ---
  private final List<LidarSpacialEventListener> listeners = new LinkedList<>();
  /* package for testing */ int limit_lo = 10; // TODO choose reasonable value
  private int usec;

  public Hdl32eSpacialProvider() {
    final double inclination = 4.0 / 3.0;
    for (int laser = 0; laser < LASERS; ++laser) {
      double theta = ORDERING[laser] * inclination * Math.PI / 180;
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

  public void addListener(LidarSpacialEventListener hdl32eSpacialEventListener) {
    listeners.add(hdl32eSpacialEventListener);
  }

  @Override
  public void timestamp(int usec, byte type) {
    this.usec = usec;
  }

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    // TODO cos/sin can be done in a lookup table!
    final double angle = rotational * ANGLE_FACTOR;
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
        LidarSpacialEvent hdl32eSpacialEvent = new LidarSpacialEvent(usec, coords, intensity);
        listeners.forEach(listener -> listener.spacial(hdl32eSpacialEvent));
      }
    }
  }
}
