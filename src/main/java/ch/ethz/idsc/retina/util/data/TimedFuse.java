// code by jph
package ch.ethz.idsc.retina.util.data;

/** the timed fuse is a recoverable watchdog
 * 
 * it performs the same as {@link PenaltyTimeout} */
public final class TimedFuse implements WatchdogInterface {
  public static WatchdogInterface notified(double tolerance_seconds) {
    TimedFuse timedFuse = new TimedFuse(tolerance_seconds);
    timedFuse.notifyWatchdog();
    return timedFuse;
  }

  public static WatchdogInterface barking(double tolerance_seconds) {
    return new TimedFuse(tolerance_seconds);
  }

  // ---
  private final long tolerance_ns;
  private long lastNotify_ns;

  /** @param tolerance_seconds */
  private TimedFuse(double tolerance_seconds) {
    tolerance_ns = Math.round(tolerance_seconds * 1E9);
    lastNotify_ns = System.nanoTime() - tolerance_ns;
  }

  @Override // from WatchdogInterface
  public void notifyWatchdog() {
    lastNotify_ns = System.nanoTime();
  }

  @Override // from WatchdogInterface
  public boolean isWatchdogBarking() {
    return tolerance_ns < System.nanoTime() - lastNotify_ns;
  }
}
