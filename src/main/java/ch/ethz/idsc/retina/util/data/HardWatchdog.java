// code by jph
package ch.ethz.idsc.retina.util.data;

/** implementation of un-recoverable watchdog
 * 
 * functionality like on a micro controller
 * except that this watchdog does not notify an interrupt
 * but simply sets a flag to true. The flag cannot be reset.
 * 
 * @see Watchdog */
public final class HardWatchdog implements Watchdog {
  /** @param timeout_seconds */
  public static Watchdog notified(double timeout_seconds) {
    return new HardWatchdog(timeout_seconds);
  }

  // ---
  private final long tolerance_ns;
  private long lastNotify_ns;
  private boolean isBlown = false;

  private HardWatchdog(double timeout_seconds) {
    tolerance_ns = Math.round(timeout_seconds * 1E9);
    lastNotify_ns = System.nanoTime();
  }

  /** resets timeout counter to zero */
  @Override // from Watchdog
  public void notifyWatchdog() {
    isBarking();
    lastNotify_ns = System.nanoTime();
  }

  /** @return true if timeout counter has ever elapsed the allowed period */
  @Override // from Watchdog
  public boolean isBarking() {
    isBlown |= tolerance_ns < System.nanoTime() - lastNotify_ns;
    return isBlown;
  }
}
