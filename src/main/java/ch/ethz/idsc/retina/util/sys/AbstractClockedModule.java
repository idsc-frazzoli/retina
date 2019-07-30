// code by swisstrolley+ and jph, gjoel
package ch.ethz.idsc.retina.util.sys;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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
  private final Semaphore semaphore = new Semaphore(1);

  /** Task to be executed for user implementation. */
  protected abstract void runAlgo();

  /** Period between runAlgo() execution.
   * 
   * @return task period as {@link Quantity} time unit, i.e. [s], [ms], or [Hz^-1] etc. */
  protected abstract Scalar getPeriod();

  @Override
  public final void launch() {
    first();
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        if (semaphore.tryAcquire())
          try {
            runAlgo(); // if this throws an exception, the semaphore is in trouble
          } finally {
            semaphore.release();
          }
      }
    };
    timer.schedule(timerTask, 0, Magnitude.MILLI_SECOND.toLong(getPeriod()));
  }

  @Override
  public final void terminate() {
    // order of launch() reversed
    timer.cancel();
    try {
      // 1 sec of grace time for task to finish
      semaphore.tryAcquire(1, TimeUnit.SECONDS);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    last();
  }
}
