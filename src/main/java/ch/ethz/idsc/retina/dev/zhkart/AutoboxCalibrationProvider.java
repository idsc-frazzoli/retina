package ch.ethz.idsc.retina.dev.zhkart;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

public abstract class AutoboxCalibrationProvider<T> implements PutProvider<T> {
  private final Queue<TimedPutEvent<T>> queue = new PriorityQueue<>();

  public boolean isIdle() {
    return queue.isEmpty();
  }

  protected void add(TimedPutEvent<T> timedPutEvent) {
    // TODO Auto-generated method stub
    queue.add(timedPutEvent);
  }

  @Override
  public final ProviderRank getProviderRank() {
    // TODO Auto-generated method stub
    return ProviderRank.CALIBRATION;
  }

  @Override
  public final Optional<T> putEvent() {
    while (!queue.isEmpty()) {
      TimedPutEvent<T> timedPutEvent = queue.peek();
      if (timedPutEvent.time_ms < System.currentTimeMillis())
        queue.poll();
      else
        return Optional.of(timedPutEvent.putEvent);
    }
    return Optional.empty();
  }
}
