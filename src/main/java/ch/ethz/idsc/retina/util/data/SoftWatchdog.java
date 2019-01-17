// code by jph
package ch.ethz.idsc.retina.util.data;

/** the timed fuse is a recoverable watchdog
 * 
 * @see Watchdog */
public final class SoftWatchdog implements Watchdog {
  public static Watchdog notified(double timeout_seconds) {
    Watchdog watchdog = new SoftWatchdog(timeout_seconds);
    watchdog.notifyWatchdog();
    return watchdog;
  }

  public static Watchdog barking(double timeout_seconds) {
    return new SoftWatchdog(timeout_seconds);
  }

  // ---
  private final long tolerance_ns;
  private long lastNotify_ns;

  /** @param timeout_seconds */
  private SoftWatchdog(double timeout_seconds) {
    tolerance_ns = Math.round(timeout_seconds * 1E9);
    lastNotify_ns = System.nanoTime() - tolerance_ns;
  }

  @Override // from Watchdog
  public void notifyWatchdog() {
    lastNotify_ns = System.nanoTime();
  }

  @Override // from Watchdog
  public boolean isBarking() {
    return tolerance_ns < System.nanoTime() - lastNotify_ns;
  }
}
