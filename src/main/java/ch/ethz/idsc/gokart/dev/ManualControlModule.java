// code by jph
package ch.ethz.idsc.gokart.dev;

import ch.ethz.idsc.retina.util.Refactor;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

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
