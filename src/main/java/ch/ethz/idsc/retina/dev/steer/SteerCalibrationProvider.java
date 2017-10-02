package ch.ethz.idsc.retina.dev.steer;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.dev.zhkart.TimedPutEvent;

public class SteerCalibrationProvider implements SteerPutProvider {
  public static final SteerCalibrationProvider INSTANCE = new SteerCalibrationProvider();
  private final Queue<TimedPutEvent<SteerPutEvent>> queue = new PriorityQueue<>();

  public boolean isIdle() {
    return queue.isEmpty();
  }

  public void schedule() {
    long timestamp = System.currentTimeMillis();
    // TODO this torque is magic constant, change it
    queue.add(new TimedPutEvent<>(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, 0.1)));
    queue.add(new TimedPutEvent<>(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, 0.2)));
    queue.add(new TimedPutEvent<>(timestamp += 2000, new SteerPutEvent(SteerPutEvent.CMD_ON, -0.1)));
    queue.add(new TimedPutEvent<>(timestamp += 1000, new SteerPutEvent(SteerPutEvent.CMD_ON, -0.2)));
  }

  @Override
  public ProviderRank getProviderRank() {
    // TODO Auto-generated method stub
    return ProviderRank.CALIBRATION;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    while (!queue.isEmpty()) {
      TimedPutEvent<SteerPutEvent> timedPutEvent = queue.peek();
      if (timedPutEvent.time_ms < System.currentTimeMillis())
        queue.poll();
      else
        return Optional.of(timedPutEvent.putEvent);
    }
    return Optional.empty();    
  }
}
