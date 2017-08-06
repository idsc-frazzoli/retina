// code by jph
package ch.ethz.idsc.retina.dev.davis._240c;

/** slows down playback to realtime */
public class RealtimeSleeper {
  private static final long MICRO = 1000000;
  // ---
  private long ref = -1;
  private long tic;
  private long sleepTotal = 0;

  /** @param time in micro seconds */
  public void now(int time) {
    if (ref == -1) {
      // initialize time
      ref = time;
      tic = System.nanoTime();
    } else {
      long act = time - ref;
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

  public double getSleepTotalSec() {
    return sleepTotal * 1e-9;
  }
}
