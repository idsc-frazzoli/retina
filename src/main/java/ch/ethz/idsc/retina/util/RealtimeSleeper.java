// code by jph
package ch.ethz.idsc.retina.util;

/** slows down playback to realtime */
public class RealtimeSleeper {
  private static final long MICRO = 1000000;
  private static final int NOT_INITIALIZED = -1;
  // ---
  private final double speed;
  private long ref = NOT_INITIALIZED;
  private long tic;
  private long sleepTotal = 0;

  /** Example:
   * speed of 0.5 will slow down playback to half realtime speed
   * 
   * @param speed */
  public RealtimeSleeper(double speed) {
    this.speed = speed;
  }

  /** @param time in micro seconds */
  public void now(int time) {
    nowNano(time * 1000L);
  }

  public void nowNano(long time) {
    if (notInitialized()) { // initialized?
      ref = time;
      tic = System.nanoTime();
    } else {
      long act = time - ref;
      long toc = System.nanoTime() - tic;
      final long sleep = Math.round(act - toc * speed);
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

  private boolean notInitialized() {
    return ref == NOT_INITIALIZED;
  }

  /** @return total sleep in nano seconds required to slow down to real time */
  public long getSleepTotal() {
    return sleepTotal;
  }

  public double getSleepTotalSec() {
    return sleepTotal * 1e-9;
  }
}
