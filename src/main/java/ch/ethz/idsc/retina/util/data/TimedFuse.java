// code by jph
package ch.ethz.idsc.retina.util.data;

import java.util.Objects;

import ch.ethz.idsc.owly.data.Stopwatch;

public class TimedFuse {
  private final double period;
  private Stopwatch stopwatch = null;
  private boolean fuse_blown = false;

  public TimedFuse(double window) {
    this.period = window;
  }

  public void register(boolean blowing_fuse) {
    if (blowing_fuse) {
      if (Objects.isNull(stopwatch))
        stopwatch = Stopwatch.started();
      fuse_blown |= period < stopwatch.display_seconds();
    } else {
      if (Objects.nonNull(stopwatch)) {
        System.out.println("reset watchdog after: " + stopwatch.display_seconds());
        stopwatch = null;
      }
    }
  }

  public boolean isBlown() {
    return fuse_blown;
  }

  public void reset() {
    fuse_blown = false;
  }
}
