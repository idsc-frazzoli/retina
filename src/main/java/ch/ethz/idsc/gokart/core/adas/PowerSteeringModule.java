// code by am and jph
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerGetListener;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.qty.Quantity;

public class PowerSteeringModule extends AbstractModule implements SteerGetListener, SteerPutProvider {
  @Override
  protected void first() {
    SteerSocket.INSTANCE.addGetListener(this);
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    SteerSocket.INSTANCE.removeGetListener(this);
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  @Override
  public void getEvent(SteerGetEvent getEvent) {
    // TODO Auto-generated method stub
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    return Optional.of(SteerPutEvent.createOn(Quantity.of(0.3, "SCT")));
  }
}
