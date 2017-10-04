// code by ej
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

public abstract class AutoboxCalibrationProvider<PE extends DataEvent> implements PutProvider<PE> {
  private final Queue<TimedPutEvent<PE>> queue = new PriorityQueue<>();

  protected AutoboxCalibrationProvider() {
  }

  public final boolean isIdle() {
    return queue.isEmpty();
  }

  protected void add(long time, PE putEvent) {
    queue.add(new TimedPutEvent<>(time, putEvent));
  }

  @Override
  public final ProviderRank getProviderRank() {
    return ProviderRank.CALIBRATION;
  }

  @Override
  public final Optional<PE> putEvent() {
    while (!queue.isEmpty()) {
      TimedPutEvent<PE> timedPutEvent = queue.peek();
      if (timedPutEvent.time_ms < System.currentTimeMillis())
        queue.poll();
      else
        return Optional.of(timedPutEvent.putEvent);
    }
    return Optional.empty();
  }
}
