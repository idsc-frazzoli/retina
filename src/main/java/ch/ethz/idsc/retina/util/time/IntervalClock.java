// code by jph
package ch.ethz.idsc.retina.util.time;

/** measure length of intervals between invocations of class methods */
// TODO OWL 029
public class IntervalClock {
  /** started upon construction */
  private long tic = System.nanoTime();

  /** @return */
  public double hertz() {
    return 1.0e9 / elapsed();
  }

  /** @return seconds since last invocation */
  public double seconds() {
    return elapsed() * 1e-9;
  }

  private long elapsed() {
    long toc = System.nanoTime();
    long tac = toc - tic;
    tic = toc;
    return tac;
  }
}
