// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

import ch.ethz.idsc.retina.dev.davis.ImuDavisEventListener;

/** slows down playback to realtime
 * 
 * is disguised as imu listener to be invoked as seldom as possible */
public class RealtimeSleeper implements ImuDavisEventListener {
  private static final long MICRO = 1000000;
  private long ref = -1;
  private long tic;
  private long sleepTotal = 0;

  @Override
  public void imu(ImuDavisEvent imuDavisEvent) {
    if (imuDavisEvent.index != 0)
      return;
    if (ref == -1) {
      // initialize time
      ref = imuDavisEvent.time;
      tic = System.nanoTime();
    } else {
      long act = imuDavisEvent.time - ref;
      act *= 1000;
      long toc = System.nanoTime() - tic;
      if (toc < act)
        try {
          long sleep = act - toc;
          long millis = sleep / MICRO;
          int nanos = (int) (sleep % MICRO);
          Thread.sleep(millis, nanos);
          sleepTotal += sleep;
        } catch (InterruptedException exception) {
          exception.printStackTrace();
        }
    }
  }

  /** @return total sleep in nano seconds required to slow down to real time */
  public long getSleepTotal() {
    return sleepTotal;
  }
}
