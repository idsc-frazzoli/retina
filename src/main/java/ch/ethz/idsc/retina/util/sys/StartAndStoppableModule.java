// code by jph
package ch.ethz.idsc.retina.util.sys;

import java.util.Objects;

import ch.ethz.idsc.retina.util.StartAndStoppable;

/** wrap for StartAndStoppable */
public class StartAndStoppableModule extends AbstractModule {
  private final StartAndStoppable startAndStoppable;

  /** @param startAndStoppable non-null
   * @throws Exception if given startAndStoppable is null */
  protected StartAndStoppableModule(StartAndStoppable startAndStoppable) {
    this.startAndStoppable = Objects.requireNonNull(startAndStoppable);
  }

  @Override // from AbstractModule
  protected final void first() {
    startAndStoppable.start();
  }

  @Override // from AbstractModule
  protected final void last() {
    startAndStoppable.stop();
  }
}
