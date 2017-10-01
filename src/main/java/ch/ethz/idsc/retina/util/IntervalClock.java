// code by jph
package ch.ethz.idsc.retina.util;

public class IntervalClock {
  private long tic = System.nanoTime(); // started upon construction

  public double hertz() {
    return 1.0e9 / elapsed();
  }

  public long elapsed() {
    long toc = System.nanoTime();
    long tac = toc - tic;
    tic = toc;
    return tac;
  }
}
