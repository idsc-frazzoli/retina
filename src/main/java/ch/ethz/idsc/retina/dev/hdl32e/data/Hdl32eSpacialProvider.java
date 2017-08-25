// code by jph
package ch.ethz.idsc.retina.dev.hdl32e.data;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eRayDataListener;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eSpacialEvent;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eSpacialEventListener;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32eStatics;

/** converts firing data to spacial events with time, 3d-coordinates and intensity */
public class Hdl32eSpacialProvider implements Hdl32eRayDataListener {
  public static final float[] IR = new float[32];
  public static final float[] IZ = new float[32];
  public static final double ANGLE_FACTOR = 2 * Math.PI / 36000.0;
  public static final double TO_METER = 0.002;
  public static final float TO_METER_FLOAT = (float) TO_METER;
  // ---
  private final List<Hdl32eSpacialEventListener> listeners = new LinkedList<>();
  /* package for testing */ int limit_lo = 0;
  private int usec;

  public Hdl32eSpacialProvider() {
    final double inclination = 4.0 / 3.0;
    for (int laser = 0; laser < LASERS; ++laser) {
      double theta = Hdl32eStatics.ORDERING[laser] * inclination * Math.PI / 180;
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

  public void addListener(Hdl32eSpacialEventListener hdl32eSpacialEventListener) {
    listeners.add(hdl32eSpacialEventListener);
  }

  @Override
  public void timestamp(int usec, byte type, byte value) {
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
        Hdl32eSpacialEvent hdl32eSpacialEvent = new Hdl32eSpacialEvent(usec, coords, intensity);
        listeners.forEach(listener -> listener.spacial(hdl32eSpacialEvent));
      }
    }
  }
}
