// code by swisstrolley+ and jph
package ch.ethz.idsc.retina.sys;

import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** AbstractClockedModule implements a timer thread which calls the algorithm
 * based on the period specified by the getPeriod() method.
 *
 * TimerTask is simple to implement, but has a couple of shortcoming. It might
 * be replaced by ScheduledThreadPoolExecutor in the future for better control
 * of the task. */
public abstract class AbstractClockedModule extends AbstractModule {
  static final ScalarUnaryOperator TO_MILLI_SECONDS = QuantityMagnitude.SI().in("ms");
  // always private
  private Timer timer = new Timer(getClass().getSimpleName()); // <- this is the thread

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
  protected final void launch() throws Exception {
    first();
    // TODO AbstractClockedModule is launched differently from AbstractModule -> not elegant!
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        runAlgo();
      }
    };
    timer.schedule(timerTask, 0, TO_MILLI_SECONDS.apply(getPeriod()).number().longValue());
  }

  @Override
  protected final void terminate() {
    // order of launch() reversed
    if (timer != null)
      timer.cancel();
    last();
  }
}
