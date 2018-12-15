// code by jph
package ch.ethz.idsc.retina.util.data;

/** the timed fuse is a recoverable watchdog
 * 
 * it performs the same as {@link PenaltyTimeout} */
public final class TimedFuse implements WatchdogInterface {
  private final long tolerance_ns;
  private long lastPacify = System.nanoTime();

  /** @param tolerance_seconds */
  public TimedFuse(double tolerance_seconds) {
    tolerance_ns = (long) (tolerance_seconds * 1E9);
  }

  @Override
  public void pacify() {
    lastPacify = System.nanoTime();
  }

  @Override
  public boolean isBlown() {
    return tolerance_ns < System.nanoTime() - lastPacify;
  }
}
