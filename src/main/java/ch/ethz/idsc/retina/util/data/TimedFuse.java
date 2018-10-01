// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.retina.sys.SafetyCritical;

/** the timed fuse is a recoverable watchdog */
@SafetyCritical
public class TimedFuse implements WatchdogInterface {
  private long lastPacify = System.nanoTime();
  private final long tolerance_ns;

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
