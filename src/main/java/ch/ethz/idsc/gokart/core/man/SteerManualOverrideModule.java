// code by gjoel
package ch.ethz.idsc.gokart.core.man;

import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

public class SteerManualOverrideModule extends AbstractModule {
  private final SteerPutProvider steerPutProvider = new SteerManualOverride();

  @Override // from AbstractModule
  protected void first() {
    SteerSocket.INSTANCE.addPutProvider(steerPutProvider);
  }

  @Override // from AbstractModule
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(steerPutProvider);
  }
}
