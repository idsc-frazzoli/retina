// code by swisstrolley+ and jph
package ch.ethz.idsc.retina.sys;

import java.util.Timer;

/** similar to {@link AbstractClockedModule} except that a thread is used instead
 * of {@link Timer}
 * 
 * AbstractEventModule should be reworked in the future to avoid using the
 * pollEvent() in a loop. This method is extremely CPU wasteful, and should be
 * replaced by Object.wait() and notifyAll() implementations instead. */
public abstract class AbstractEventModule extends AbstractModule {
  private Thread thread;
  private final int sleepTime = 20;

  /** Task to be executed for user implementation. */
  protected abstract void runAlgo();

  /** Initialisation for user implementation. Runs before runEventModule() is ever
   * called. */
  @Override
  protected abstract void first() throws Exception;

  /** Graceful closure for user implementation Runs after runEventModule() is
   * terminated for good. */
  @Override
  protected abstract void last();

  /** Event which triggers the execution.
   * 
   * @return boolean */
  protected abstract boolean pollEvent();

  @Override
  protected final void launch() throws Exception {
    first();
    thread = new Thread(() -> {
      while (!thread.isInterrupted()) {
        if (pollEvent()) {
          runAlgo();
        } else {
          try {
            Thread.sleep(sleepTime);
          } catch (InterruptedException e) {
            return;
          }
        }
      }
    });
    thread.setName(getClass().getSimpleName());
    thread.start();
    // ModuleAuto.watch(getClass(), this);
  }

  @Override
  protected final void terminate() {
    // order of launch() reversed
    if (thread != null) {
      thread.interrupt();
    }
    last();
  }
}