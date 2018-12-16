// code by jph
package ch.ethz.idsc.retina.util.time;

import java.util.Objects;

/** functionality is used to slow down playback to realtime
 * 
 * 1) nano seconds 2) long encoding */
public class RealtimeSleeper {
  private static final long MICRO = 1000000;
  // ---
  /** factor of real-time */
  private final double factor;
  private Long ref = null;
  private long tic;
  private long sleepTotal = 0;

  /** Example: speed of 0.5 will slow down playback to half real-time speed
   * 
   * @param factor */
  public RealtimeSleeper(double factor) {
    this.factor = factor;
  }

  /** @param time in nano seconds
   * @see System#nanoTime() */
  public void now(long time) {
    if (notInitialized()) {
      ref = time;
      tic = System.nanoTime();
    } else {
      long act = time - ref;
      long toc = System.nanoTime() - tic;
      final long sleep = Math.round(act - toc * factor);
      if (0 < sleep)
        try {
          long millis = sleep / MICRO;
          int nanos = (int) (sleep % MICRO);
          Thread.sleep(millis, nanos);
          sleepTotal += sleep;
        } catch (InterruptedException exception) {
          exception.printStackTrace();
        }
    }
  }

  /** timestamp used in pcap format
   * 
   * @param sec
   * @param usec micro seconds in range [0, 1, ..., 999999] */
  public void now(int sec, int usec) {
    now(sec * 1000_000_000L + usec * 1000L);
  }

  private boolean notInitialized() {
    return Objects.isNull(ref);
  }

  /** @return total sleep in nano seconds required to slow down to real time */
  public long getSleepTotal() {
    return sleepTotal;
  }

  public double getSleepTotalSec() {
    return sleepTotal * 1e-9;
  }
}
