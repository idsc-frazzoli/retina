// code by jph
package ch.ethz.idsc.retina.davis._240c;

import ch.ethz.idsc.retina.davis.ImuDavisEventListener;
import ch.ethz.idsc.retina.util.IntRealtimeSleeper;

/** slows down playback to realtime
 * 
 * is disguised as imu listener to be invoked as seldom as possible */
public class EventRealtimeSleeper implements ImuDavisEventListener {
  private final IntRealtimeSleeper realtimeSleeper;

  public EventRealtimeSleeper(double speed) {
    realtimeSleeper = new IntRealtimeSleeper(speed);
  }

  @Override
  public void imu(ImuDavisEvent imuDavisEvent) {
    if (imuDavisEvent.index != 0)
      return;
    realtimeSleeper.now(imuDavisEvent.time);
  }
}
