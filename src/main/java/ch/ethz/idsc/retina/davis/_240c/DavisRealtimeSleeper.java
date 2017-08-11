// code by jph
package ch.ethz.idsc.retina.davis._240c;

import ch.ethz.idsc.retina.davis.DavisImuEventListener;
import ch.ethz.idsc.retina.util.IntRealtimeSleeper;

/** slows down playback to realtime
 * 
 * is disguised as imu listener to be invoked as seldom as possible */
public class DavisRealtimeSleeper implements DavisImuEventListener {
  private final IntRealtimeSleeper realtimeSleeper;

  public DavisRealtimeSleeper(double speed) {
    realtimeSleeper = new IntRealtimeSleeper(speed);
  }

  @Override
  public void imu(DavisImuEvent imuDavisEvent) {
    if (imuDavisEvent.index != 0)
      return;
    realtimeSleeper.now(imuDavisEvent.time);
  }
}
