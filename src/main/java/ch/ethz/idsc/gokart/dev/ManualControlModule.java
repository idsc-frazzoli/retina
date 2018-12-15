// code by jph
package ch.ethz.idsc.gokart.dev;

import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.meta.Refactor;

@Refactor // TODO JAN
class ManualControlModule extends AbstractModule {
  @Override
  protected void first() throws Exception {
    ManualControlSingleton.INSTANCE.start();
  }

  @Override
  protected void last() {
    ManualControlSingleton.INSTANCE.stop();
  }
}
