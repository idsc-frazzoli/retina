// code by jph
package ch.ethz.idsc.retina.dev.urg04lxug01;

import ch.ethz.idsc.retina.util.RealtimeSleeper;

public class RealtimeUrg04lxListener implements Urg04lxListener {
  private static final long PERIOD_NANOS = 100_000_000L;
  // ---
  private final RealtimeSleeper realtimeSleeper;
  private int lines = 0;

  public RealtimeUrg04lxListener(double sleep) {
    realtimeSleeper = new RealtimeSleeper(sleep);
  }

  @Override
  public void urg(String line) {
    realtimeSleeper.now(lines * PERIOD_NANOS); // FIXME the time should be available in `line`
    ++lines;
  }
}
