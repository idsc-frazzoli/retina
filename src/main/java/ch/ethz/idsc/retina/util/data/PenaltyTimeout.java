// code by jph
package ch.ethz.idsc.retina.util.data;

/** recoverable triggered time interval */
// TODO JPH unify with TimedFuse
public final class PenaltyTimeout {
  private final long tolerance_ns;
  private long lastNotify_ns;

  /** @param timeout_seconds */
  public PenaltyTimeout(double timeout_seconds) {
    tolerance_ns = Math.round(timeout_seconds * 1E9);
    lastNotify_ns = System.nanoTime() - tolerance_ns;
  }

  /** resets timeout counter to zero */
  public void notifyWatchdog() {
    lastNotify_ns = System.nanoTime();
  }

  /** @return true if timeout counter has ever elapsed the allowed period */
  public boolean isBarking() {
    return tolerance_ns < System.nanoTime() - lastNotify_ns;
  }
}
