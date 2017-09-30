// code by nisaak, rvmoos, and jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.dev.zhkart.TimedPutEvent;

public enum LinmotCalibrationProvider implements LinmotPutProvider {
  INSTANCE;
  // ---
  private final Queue<TimedPutEvent<LinmotPutEvent>> queue = new PriorityQueue<>();

  public boolean isIdle() {
    return queue.isEmpty();
  }

  public void schedule() {
    long timestamp = System.currentTimeMillis();
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent( //
          LinmotPutHelper.CMD_ERR_ACK, //
          LinmotPutHelper.MC_ZEROS);
      timestamp += 200;
      queue.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
    {
      timestamp += 200;
      queue.add(new TimedPutEvent<>(timestamp, LinmotPutHelper.OFF_MODE_EVENT));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent( //
          LinmotPutHelper.CMD_HOME, //
          LinmotPutHelper.MC_ZEROS);
      timestamp += 4000;
      queue.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent( //
          LinmotPutHelper.CMD_OPERATION, //
          LinmotPutHelper.MC_ZEROS); //
      timestamp += 200;
      queue.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
    {
      LinmotPutEvent linmotPutEvent = new LinmotPutEvent( //
          LinmotPutHelper.CMD_OPERATION, //
          LinmotPutHelper.MC_POSITION); //
      timestamp += 200;
      queue.add(new TimedPutEvent<>(timestamp, linmotPutEvent));
    }
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.CALIBRATION;
  }

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    while (!queue.isEmpty()) {
      TimedPutEvent<LinmotPutEvent> timedPutEvent = queue.peek();
      if (timedPutEvent.time_ms < System.currentTimeMillis())
        queue.poll();
      else
        return Optional.of(timedPutEvent.putEvent);
    }
    return Optional.empty();
  }
}
