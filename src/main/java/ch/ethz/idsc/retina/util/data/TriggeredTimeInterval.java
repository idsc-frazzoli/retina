// code by jph
package ch.ethz.idsc.retina.util.data;

import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.retina.sys.SafetyCritical;

/** represents an interval in time */
@SafetyCritical
public class TriggeredTimeInterval {
  private final double duration_seconds;
  private boolean isBlown = false;
  private final Stopwatch stopwatch = Stopwatch.stopped();

  public TriggeredTimeInterval(double duration_seconds) {
    this.duration_seconds = duration_seconds;
  }

  /** the first invocation of {@link #panic()} triggers the
   * time interval. Subsequent invocations do nothing. */
  public void panic() {
    if (!isBlown) {
      isBlown = true;
      stopwatch.start();
    }
  }

  /** @return true if present time is inside the triggered time interval */
  public boolean isActive() {
    return isBlown && stopwatch.display_seconds() < duration_seconds;
  }
}
