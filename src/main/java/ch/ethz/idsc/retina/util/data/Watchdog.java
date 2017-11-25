// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.owl.data.Stopwatch;

/** functionality like on a micro controller
 * except that this watchdog does not notify an interrupt
 * but simply sets a flag to true that cannot be reset */
public class Watchdog {
  private final Stopwatch stopwatch = Stopwatch.started();
  private final double timeout_seconds;
  private boolean isBlown = false;

  public Watchdog(double timeout_seconds) {
    this.timeout_seconds = timeout_seconds;
  }

  public void pacify() {
    stopwatch.stop();
    stopwatch.resetToZero();
    stopwatch.start();
  }

  public boolean isBlown() {
    isBlown |= timeout_seconds < stopwatch.display_seconds();
    return isBlown;
  }
}
