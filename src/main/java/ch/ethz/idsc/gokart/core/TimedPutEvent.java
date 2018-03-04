// code by nisaak and jph
package ch.ethz.idsc.gokart.core;

/** message in schedule of calibration procedure */
/* package */ final class TimedPutEvent<T> implements Comparable<TimedPutEvent<T>> {
  private final long time_ms;
  private final T putEvent;

  /** time_ms is typically obtained by {@link System#currentTimeMillis()}
   * 
   * @param time_ms absolute time stamp until when to execute given putEvent.
   * @param putEvent */
  public TimedPutEvent(long time_ms, T putEvent) {
    this.time_ms = time_ms;
    this.putEvent = putEvent;
  }

  @Override // from Comparable
  public int compareTo(TimedPutEvent<T> timedPutEvent) {
    return Long.compare(time_ms, timedPutEvent.time_ms);
  }

  /** @param now_ms
   * @return true if TimedPutEvent::time_ms is smaller than given now_ms */
  public boolean isExpired(long now_ms) {
    return time_ms < now_ms;
  }

  /** @return calibration command */
  public T putEvent() {
    return putEvent;
  }
}
