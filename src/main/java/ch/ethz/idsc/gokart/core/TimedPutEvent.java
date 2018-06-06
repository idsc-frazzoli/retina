// code by nisaak and jph
package ch.ethz.idsc.gokart.core;

import java.util.function.Supplier;

/** message in schedule of calibration procedure */
/* package */ final class TimedPutEvent<T> implements Comparable<TimedPutEvent<T>> {
  private final long time_ms;
  private final Supplier<T> supplier;

  /** time_ms is typically obtained by {@link System#currentTimeMillis()}
   * 
   * @param time_ms absolute time stamp until when to execute given putEvent.
   * @param supplier */
  public TimedPutEvent(long time_ms, Supplier<T> supplier) {
    this.time_ms = time_ms;
    this.supplier = supplier;
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
    return supplier.get();
  }
}
