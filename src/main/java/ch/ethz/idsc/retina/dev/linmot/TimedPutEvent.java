// code by nisaak and jph
package ch.ethz.idsc.retina.dev.linmot;

public class TimedPutEvent<T> implements Comparable<TimedPutEvent<T>> {
  public final long time_ms;
  public final T linmotPutEvent;

  public TimedPutEvent(long time_ms, T linmotPutEvent) {
    this.time_ms = time_ms;
    this.linmotPutEvent = linmotPutEvent;
  }

  @Override
  public int compareTo(TimedPutEvent<T> arg0) {
    return Long.compare(time_ms, arg0.time_ms);
  }
}
