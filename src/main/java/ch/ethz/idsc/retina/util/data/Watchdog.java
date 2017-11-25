// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.retina.sys.SafetyCritical;

/** functionality like on a micro controller
 * except that this watchdog does not notify an interrupt
 * but simply sets a flag to true that cannot be reset */
@SafetyCritical
public class Watchdog {
  private final Stopwatch stopwatch = Stopwatch.started();
  private final double timeout_seconds;
  private boolean isBlown = false;

  /** @param timeout_seconds */
  public Watchdog(double timeout_seconds) {
    this.timeout_seconds = timeout_seconds;
  }

  /** resets timeout counter to zero */
  public void pacify() {
    isBlown();
    stopwatch.stop();
    stopwatch.resetToZero();
    stopwatch.start();
  }

  /** @return true if timeout counter has ever elapsed the allowed period */
  public boolean isBlown() {
    isBlown |= timeout_seconds < stopwatch.display_seconds();
    return isBlown;
  }
}
