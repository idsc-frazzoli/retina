// code by swisstrolley+ and jph
package ch.ethz.idsc.retina.util.sys;

import java.util.Date;
import java.util.Objects;

/** Abstract Module is the thread starter module. It tries to launch the
 * algorithm implemented in {@link AbstractEventModule} or
 * {@link AbstractClockedModule}. After a successful launch, this thread would
 * automatically terminate. Should there be an exception thrown while starting
 * the modules, this class would try to restart it again. */
public abstract class AbstractModule {
  private static final long RETRY_PERIOD = 5000;
  /** this thread is only used to call launch() thread will terminate once launch()
   * is over. */
  private Thread thread; // keep private

  /** Used by task manager to launch the modules. */
  protected void launch() throws Exception {
    final Object object = this;
    thread = new Thread(() -> {
      while (!thread.isInterrupted())
        try {
          first();
          return; // <- this will exit thread
        } catch (Exception e) {
          System.err.println(new Date() + " Abstract Module: failed launch: " + object.getClass().getSimpleName());
          try {
            Thread.sleep(RETRY_PERIOD);
          } catch (InterruptedException e1) {
            // thread.interrupt();
            return;
          }
        }
    });
    thread.setName(getClass().getSimpleName());
    thread.start();
  }

  /** Used by task manager to terminate the modules. */
  protected void terminate() {
    // order of launch() reversed
    if (Objects.nonNull(thread))
      thread.interrupt();
    last();
  }

  /** function should initialize everything quickly and then return.
   * If the procedure encounters problems (such as hardware failure,
   * files missing, ...) an exception is thrown.
   *
   * In particular, the function should not block indefinitely.
   *
   * @throws Exception */
  protected abstract void first() throws Exception;

  /** function undoes the initialization of function first().
   * Typically the de-initialization is done in reverse order. */
  protected abstract void last();
}
