// code by jph
package ch.ethz.idsc.retina.util.data;

/** recoverable triggered time interval */
// TODO JPH unify with TimedFuse
public final class PenaltyTimeout {
  private final long timeout_ns;
  private long lastPenalty;

  /** @param timeout_seconds */
  public PenaltyTimeout(double timeout_seconds) {
    timeout_ns = Math.round(timeout_seconds * 1E9);
    lastPenalty = System.nanoTime() - timeout_ns;
  }

  /** resets timeout counter to zero */
  public void flagPenalty() {
    lastPenalty = System.nanoTime();
  }

  /** @return true if timeout counter has ever elapsed the allowed period */
  public boolean isPenalty() {
    return System.nanoTime() - lastPenalty < timeout_ns;
  }
}
