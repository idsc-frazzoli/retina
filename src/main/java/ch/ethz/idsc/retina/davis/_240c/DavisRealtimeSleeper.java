// code by jph
package ch.ethz.idsc.retina.davis._240c;

import ch.ethz.idsc.retina.davis.DavisImuListener;
import ch.ethz.idsc.retina.util.time.IntRealtimeSleeper;

/** slows down playback to realtime
 * 
 * is disguised as imu listener to be invoked as seldom as possible */
public class DavisRealtimeSleeper implements DavisImuListener {
  private final IntRealtimeSleeper realtimeSleeper;

  public DavisRealtimeSleeper(double speed) {
    realtimeSleeper = new IntRealtimeSleeper(speed);
  }

  @Override
  public void davisImu(DavisImuEvent davisImuEvent) {
    if (davisImuEvent.index != 0)
      return;
    realtimeSleeper.now(davisImuEvent.time);
  }
}
