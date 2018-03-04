// code by ej
package ch.ethz.idsc.gokart.core;

import java.util.Objects;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;

import ch.ethz.idsc.owl.math.state.ProviderRank;
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
  /** schedule calibration commands at most 10[s] into the future */
  private static final long MAX_FUTURE_MS = 10_000;
  // ---
  /** queue of calibration commands */
  private final Queue<TimedPutEvent<PE>> queue = new PriorityQueue<>();

  protected AutoboxCalibrationProvider() {
  }

  /** removes expired commands from queue */
  private synchronized void removeExpired() {
    final long now_ms = now_ms();
    TimedPutEvent<PE> timedPutEvent = queue.peek(); // <- null if queue is empty
    while (Objects.nonNull(timedPutEvent)) {
      if (timedPutEvent.isExpired(now_ms)) {
        queue.poll();
        timedPutEvent = queue.peek();
      } else
        break;
    }
  }

  /** @return true if queue of calibration commands is empty */
  public final boolean isIdle() {
    removeExpired();
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

  @Override // from PutProvider
  public final ProviderRank getProviderRank() {
    return ProviderRank.CALIBRATION;
  }

  @Override // from PutProvider
  public final Optional<PE> putEvent() {
    removeExpired(); // <- mandatory
    // ---
    TimedPutEvent<PE> timedPutEvent = queue.peek(); // <- null if queue is empty
    return Objects.nonNull(timedPutEvent) //
        ? Optional.of(timedPutEvent.putEvent())
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

  /** @return absolute timestamp now in milli seconds */
  protected static long now_ms() {
    return System.currentTimeMillis();
  }
}
