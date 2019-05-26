// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import java.util.Objects;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/* package */ abstract class VelodyneLcmServerModule extends AbstractModule {
  private final StartAndStoppable startAndStoppable;

  public VelodyneLcmServerModule(StartAndStoppable startAndStoppable) {
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
