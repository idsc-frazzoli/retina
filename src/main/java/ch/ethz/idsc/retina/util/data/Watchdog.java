// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.tensor.io.Timing;

/** implementation of un-recoverable watchdog
 * 
 * functionality like on a micro controller
 * except that this watchdog does not notify an interrupt
 * but simply sets a flag to true. The flag cannot be reset. */
public final class Watchdog implements WatchdogInterface {
  private final double timeout_seconds;
  // ---
  private final Timing timing = Timing.started();
  private boolean isBlown = false;

  /** @param timeout_seconds */
  public Watchdog(double timeout_seconds) {
    this.timeout_seconds = timeout_seconds;
  }

  /** resets timeout counter to zero */
  @Override // from WatchdogInterface
  public void notifyWatchdog() {
    isWatchdogBarking();
    timing.stop();
    timing.resetToZero();
    timing.start();
  }

  /** @return true if timeout counter has ever elapsed the allowed period */
  @Override // from WatchdogInterface
  public boolean isWatchdogBarking() {
    isBlown |= timeout_seconds < timing.seconds();
    return isBlown;
  }
}
