// code by swisstrolley+ and jph
package ch.ethz.idsc.retina.sys;

import java.util.Timer;
import java.util.TimerTask;

/** AbstractClockedModule implements a timer thread which calls the algorithm
 * based on the period specified by the getPeriod() method.
 *
 * TimerTask is simple to implement, but has a couple of shortcoming. It might
 * be replaced by ScheduledThreadPoolExecutor in the future for better control
 * of the task. */
public abstract class AbstractClockedModule extends AbstractModule {
  // always private
  private Timer timer = new Timer(getClass().getSimpleName()); // <- this is the thread
  // private volatile long lastRun = 0;
  // private volatile long timeOut = 0;
  // private volatile boolean firstDone = false;

  /** Task to be executed for user implementation. */
  protected abstract void runAlgo();

  /** Initialisation for user implementation. Runs before runEventModule() is
   * ever called. */
  @Override
  protected abstract void first() throws Exception;

  /** Graceful closure for user implementation Runs after runEventModule() is
   * terminated for good. */
  @Override
  protected abstract void last();

  /** Period between runClockedModule execution.
   * 
   * @return task period in seconds */
  protected abstract double getPeriod();

  @Override
  protected final void launch() throws Exception {
    first();
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        runAlgo();
        // lastRun = SystemTimestamp.get();
      }
    };
    // timeOut = Math.round((getPeriod() * 1000) * 3);
    // ModuleAuto.watch(getClass(), this);
    timer.schedule(timerTask, 0, Math.round((getPeriod() * 1000)));
  }

  @Override
  protected final void terminate() {
    // order of launch() reversed
    if (timer != null) {
      timer.cancel();
    }
    last();
  }
  // protected final boolean isLagging() {
  // return (SystemTimestamp.get() - lastRun > timeOut) && (timer != null) &&
  // firstDone;
  // }
}
