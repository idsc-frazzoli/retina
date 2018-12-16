// code by swisstrolley+ and jph
package ch.ethz.idsc.retina.util.sys;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** AbstractClockedModule implements a timer thread which calls the algorithm
 * based on the period specified by the getPeriod() method.
 *
 * TimerTask is simple to implement, but has a couple of shortcoming. It might
 * be replaced by ScheduledThreadPoolExecutor in the future for better control
 * of the task. */
public abstract class AbstractClockedModule extends AbstractModule {
  private final Timer timer = new Timer(getClass().getSimpleName()); // <- this is the thread

  /** Task to be executed for user implementation. */
  protected abstract void runAlgo();

  /** Initialization for user implementation.
   * Runs before runEventModule() is ever called. */
  @Override
  protected abstract void first() throws Exception;

  /** Graceful closure for user implementation Runs after runEventModule() is
   * terminated for good. */
  @Override
  protected abstract void last();

  /** Period between runClockedModule execution.
   * 
   * @return task period as {@link Quantity} time unit, i.e. [s], [ms], or [Hz^-1] etc. */
  protected abstract Scalar getPeriod();

  @Override
  public final void launch() throws Exception {
    first();
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        runAlgo();
      }
    };
    timer.schedule(timerTask, 0, Magnitude.MILLI_SECOND.toLong(getPeriod()));
  }

  @Override
  public final void terminate() {
    // order of launch() reversed
    if (Objects.nonNull(timer))
      timer.cancel();
    last();
  }
}
