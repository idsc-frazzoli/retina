// code by ej
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

public abstract class AutoboxCalibrationProvider<PE extends DataEvent> implements PutProvider<PE> {
  private final Queue<TimedPutEvent<PE>> queue = new PriorityQueue<>();

  // TODO EJDH protected constructor
  public final boolean isIdle() {
    return queue.isEmpty();
  }

  // TODO EJDH function takes 2 parameters long, putevent, create timed... inside function
  protected void add(TimedPutEvent<PE> timedPutEvent) {
    queue.add(timedPutEvent);
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
