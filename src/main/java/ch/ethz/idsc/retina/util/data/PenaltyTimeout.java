// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.retina.sys.SafetyCritical;

/**  */
@SafetyCritical
public class PenaltyTimeout {
  private final long timeout_ns;
  private long lastPenalty_ns;

  /** @param timeout_seconds */
  public PenaltyTimeout(double timeout_seconds) {
    timeout_ns = Math.round(timeout_seconds * 1E9);
    lastPenalty_ns = System.nanoTime() - timeout_ns;
  }

  /** resets timeout counter to zero */
  public void flagPenalty() {
    lastPenalty_ns = System.nanoTime();
  }

  /** @return true if timeout counter has ever elapsed the allowed period */
  public boolean isPenalty() {
    return System.nanoTime() < lastPenalty_ns + timeout_ns;
  }
}
