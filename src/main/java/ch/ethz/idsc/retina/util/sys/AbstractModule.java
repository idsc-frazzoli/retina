// code by swisstrolley+ and jph
package ch.ethz.idsc.retina.util.sys;

/** Abstract Module is the thread starter module. It tries to launch the
 * algorithm implemented in {@link AbstractEventModule} or
 * {@link AbstractClockedModule}. After a successful launch, this thread would
 * automatically terminate. Should there be an exception thrown while starting
 * the modules, this class would try to restart it again. */
public abstract class AbstractModule {
  /** Used by task manager to launch the modules. */
  protected void launch() throws Exception {
    first();
  }

  /** Used by task manager to terminate the modules. */
  protected void terminate() {
    last();
  }

  /** function should initialize everything quickly and then return.
   * If the procedure encounters problems (such as hardware failure,
   * files missing, ...) an exception is thrown.
   *
   * In particular, the function should not block indefinitely.
   *
   * @throws Exception */
  protected abstract void first();

  /** function undoes the initialization of function first().
   * Typically the de-initialization is done in reverse order. */
  protected abstract void last();
}
