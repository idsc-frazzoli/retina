// code by am, jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** applies contant torque to */
public final class ConstantTorqueSteerModule extends AbstractModule implements SteerPutProvider {
  @Override
  protected void first() {
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  @Override // from SteerPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }

  @Override // from SteerPutProvider
  public Optional<SteerPutEvent> putEvent() {
    return Optional.of(SteerPutEvent.createOn(HapticSteerConfig.GLOBAL.constantTorque));
  }
}
