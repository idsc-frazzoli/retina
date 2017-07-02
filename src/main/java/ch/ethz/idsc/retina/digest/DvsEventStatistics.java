// code by jph
package ch.ethz.idsc.retina.digest;

import ch.ethz.idsc.retina.core.DvsEvent;

public class DvsEventStatistics implements DvsEventDigest {
  private static final int INTERVAL = 1000000;
  // ---
  private final long first = System.currentTimeMillis();
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

  public void printSummary() {
    long dif = System.currentTimeMillis() - first;
    System.out.println("total= " + total);
    System.out.println("time = " + (dif * 1e-3) + " [sec]");
    System.out.println("perf = " + Math.round(total / (dif)) + "k [events/sec]");
  }
}
