// code by ej
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import ch.ethz.idsc.retina.dev.linmot.LinmotCalibrationProvider;
import ch.ethz.idsc.retina.dev.misc.MiscIgnitionProvider;
import ch.ethz.idsc.retina.dev.steer.SteerCalibrationProvider;

/** base class for all calibration providers
 * 
 * <p>A calibration provider should be a singleton instance
 * that is appended to list of providers of the relevant
 * {@link AutoboxSocket}
 * 
 * <p>Examples of calibration providers are
 * {@link LinmotCalibrationProvider}
 * {@link SteerCalibrationProvider}
 * {@link MiscIgnitionProvider} */
public abstract class AutoboxCalibrationProvider<PE extends DataEvent> implements PutProvider<PE> {
  private static final long MAX_FUTURE_MS = 10_000;
  // ---
  private final Queue<TimedPutEvent<PE>> queue = new PriorityQueue<>();

  protected AutoboxCalibrationProvider() {
  }

  private synchronized void removeOld() {
    TimedPutEvent<PE> timedPutEvent = queue.peek(); // <- null if queue is empty
    long now_ms = now_ms();
    while (Objects.nonNull(timedPutEvent)) {
      if (timedPutEvent.time_ms < now_ms) {
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

  /** @param time_ms until given putEvent should be provided by calibration procedure
   * @param putEvent */
  protected synchronized final void eventUntil(long time_ms, PE putEvent) {
    long now = now_ms();
    /** event is between now and 10[s] into the future */
    if (now < time_ms && time_ms < now + MAX_FUTURE_MS)
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
    TimedPutEvent<PE> timedPutEvent = queue.peek(); // <- null if queue is empty
    return Objects.nonNull(timedPutEvent) //
        ? Optional.of(timedPutEvent.putEvent)
        : Optional.empty();
  }

  /** schedules implementation specific calibration routine
   * if a previous schedule is still being processed no further
   * messages are appended to the queue */
  public synchronized final void schedule() {
    if (isIdle())
      protected_schedule();
    else
      new RuntimeException().printStackTrace();
  }

  /** function invokes {@link #eventUntil(long, DataEvent)} */
  protected abstract void protected_schedule();

  protected static long now_ms() {
    return System.currentTimeMillis();
  }
}
