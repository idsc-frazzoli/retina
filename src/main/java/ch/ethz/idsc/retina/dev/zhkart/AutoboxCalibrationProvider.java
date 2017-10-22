// code by ej
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

public abstract class AutoboxCalibrationProvider<PE extends DataEvent> implements PutProvider<PE> {
  private final Queue<TimedPutEvent<PE>> queue = new PriorityQueue<>();

  protected AutoboxCalibrationProvider() {
  }

  private synchronized void removeOld() {
    TimedPutEvent<PE> timedPutEvent = queue.peek();
    long now = now();
    while (Objects.nonNull(timedPutEvent)) {
      if (timedPutEvent.time_ms < now) {
        queue.poll();
        timedPutEvent = queue.peek();
      } else
        break;
    }
  }

  public final boolean isIdle() {
    removeOld();
    // ---
    return queue.isEmpty();
  }

  protected synchronized final void eventUntil(long time_ms, PE putEvent) {
    long now = now();
    // event is between now and 10[s] into the future
    if (now < time_ms && time_ms < now + 10_000)
      queue.add(new TimedPutEvent<>(time_ms, putEvent));
    else
      System.err.println("event is outside permitted time window");
  }

  @Override
  public final ProviderRank getProviderRank() {
    return ProviderRank.CALIBRATION;
  }

  @Override
  public final Optional<PE> putEvent() {
    removeOld(); // <- mandatory
    // ---
    TimedPutEvent<PE> timedPutEvent = queue.peek();
    if (Objects.nonNull(timedPutEvent))
      return Optional.of(timedPutEvent.putEvent);
    return Optional.empty();
  }

  public synchronized final void schedule() {
    if (isIdle())
      protected_schedule();
    else
      new RuntimeException().printStackTrace();
  }

  protected abstract void protected_schedule();

  protected static long now() {
    return System.currentTimeMillis();
  }
}
