// code by jph
package ch.ethz.idsc.retina.lidar;

import java.util.LinkedList;
import java.util.List;

public abstract class VelodyneSpacialProvider implements LidarSpacialProvider {
  /** initial value of limit lo has interpretation as distance in meters
   * by multiplication with the factor VelodyneStatics.TO_METER == 0.002
   * 
   * <p>Therefore, a value of 10 corresponds to a distance of 0.02[m] */
  public static final int INITIAL_LIMIT_LO = 10;
  // ---
  protected final List<LidarSpacialListener> listeners = new LinkedList<>();
  protected int usec;
  protected int limit_lo = INITIAL_LIMIT_LO;

  @Override // from LidarSpacialProvider
  public final void addListener(LidarSpacialListener lidarSpacialListener) {
    listeners.add(lidarSpacialListener);
  }

  @Override // from LidarRayDataListener
  public final void timestamp(int usec, int type) {
    this.usec = usec;
  }

  /** quote from the user's manual, p.8:
   * "the minimum return distance for the HDL-32E is approximately 1 meter.
   * ignore returns closer than this"
   * 
   * However, we find that the sensor provides correct ranges below 1[m]
   * in office and hangar conditions.
   * 
   * @param closest_m in [m] */
  public final void setLimitLo(double closest_m) {
    limit_lo = (int) (closest_m / VelodyneStatics.TO_METER);
  }

  /** @return cutoff threshold in [m] */
  public final double getLimitLo() {
    return limit_lo * VelodyneStatics.TO_METER;
  }
}
