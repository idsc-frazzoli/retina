// code by nisaak and jph
package ch.ethz.idsc.retina.dev.linmot;

public class TimedPutEvent<T> implements Comparable<TimedPutEvent<T>> {
  public final long time_ms;
  public final T putEvent;

  public TimedPutEvent(long time_ms, T putEvent) {
    this.time_ms = time_ms;
    this.putEvent = putEvent;
  }

  @Override
  public int compareTo(TimedPutEvent<T> arg0) {
    return Long.compare(time_ms, arg0.time_ms);
  }
}
