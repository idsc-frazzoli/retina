// code by jph
package ch.ethz.idsc.retina.dev.hdl32e.data;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringDataListener;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eFiringListener;

/** collects a complete 360 rotation */
@Deprecated
public class Hdl32eAngularFiringCollector implements Hdl32eFiringDataListener {
  /** the highway scene has ~140000 coordinates */
  public static final int MAX_COORDINATES = 2304 * 32 * 3; // == 221184
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
  // pi/180/1000
  public static final float[] IR = new float[32];
  public static final float[] IZ = new float[32];
  public static final double ANGLE_FACTOR = Math.PI / 18000.0; // 100th of a degree
  // ---
  private final Hdl32eFiringListener hdl32eFiringListener;

  // TODO configure to also allow for subscription of other than 360 fov
  public Hdl32eAngularFiringCollector(Hdl32eFiringListener hdl32eFiringListener) {
    final double INCLINATION_FACTOR = 4.0 / 3.0;
    for (int laser = 0; laser < LASERS; ++laser) {
      double theta = ORDERING[laser] * INCLINATION_FACTOR * Math.PI / 180;
      IR[laser] = (float) Math.cos(theta);
      IZ[laser] = (float) Math.sin(theta);
    }
    this.hdl32eFiringListener = hdl32eFiringListener;
  }

  private int rotational_last = -1;
  private int max = 0;
  private float[] position_data = new float[MAX_COORDINATES];
  private int position_index = -1;

  @Override
  public void scan(int rotational, ByteBuffer byteBuffer) {
    if (rotational < rotational_last) {
      hdl32eFiringListener.digest(position_data, position_index + 1);
      position_index = -1;
    }
    rotational_last = rotational;
    // TODO cos/sin can be done in a lookup table!
    final double angle = rotational * ANGLE_FACTOR;
    float dx = (float) Math.cos(angle);
    float dy = (float) Math.sin(angle);
    for (int laser = 0; laser < LASERS; ++laser) {
      int distance = byteBuffer.getShort() & 0xffff;
      // System.out.println(distance);
      if (max < distance) {
        max = distance;
        // System.out.println(distance);
      }
      @SuppressWarnings("unused")
      int intensity = byteBuffer.get(); // TODO intensity is not forwarded at the moment
      // quote from the user's manual, p.8:
      // "the minimum return distance for the HDL-32E is approximately 1 meter.
      // ignore returns closer than this"
      if (500 <= distance) {
        // TODO also filter too far?
        // "report distance to the nearest .2 cm" => 2 mm
        float range = distance * 0.002f; // convert to [meter]
        float px = IR[laser] * range * dx;
        float py = IR[laser] * range * dy;
        float pz = IZ[laser] * range;
        position_data[++position_index] = px;
        position_data[++position_index] = py;
        position_data[++position_index] = pz;
      } // else too close => ignore
    }
  }

  @Override
  public void timestamp(int usec, byte type, byte value) {
    // ---
  }
}
