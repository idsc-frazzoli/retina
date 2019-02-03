// code by jph
package ch.ethz.idsc.retina.dvs.digest;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.sca.Round;

public class DvsEventStatistics implements DvsEventDigest {
  private static final int INTERVAL = 1_000_000;
  // ---
  private final Timing timing = Timing.started();
  private int mark_us = INTERVAL;
  private long total_mark = 0;
  private long total = 0;

  @Override
  public void digest(DvsEvent dvsEvent) {
    long time = dvsEvent.time_us;
    if (mark_us < time) {
      System.out.println("evt/sec = " + (total - total_mark));
      mark_us += INTERVAL;
      total_mark = total;
    }
    ++total;
  }

  public long total() {
    return total;
  }

  public void printSummary() {
    double seconds = timing.seconds();
    System.out.println("total= " + total + " [events]");
    System.out.println("time = " + DoubleScalar.of(seconds).map(Round._4) + " [sec]");
    System.out.println("perf = " + Math.round(total / (seconds)) + " [events/sec]");
  }
}
