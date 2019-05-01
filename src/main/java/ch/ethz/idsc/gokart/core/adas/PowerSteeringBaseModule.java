// code by am, jph 
package ch.ethz.idsc.gokart.core.adas;

import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

public abstract class PowerSteeringBaseModule extends AbstractModule implements SteerGetListener, SteerPutProvider {
  @Override
  protected final void first() {
    SteerSocket.INSTANCE.addGetListener(this);
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected final void last() {
    SteerSocket.INSTANCE.removeGetListener(this);
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  @Override
  public final ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }
}
