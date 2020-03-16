// code by jph, gjoel
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;

public final class SteerManualOverride implements SteerPutProvider {
  @Override
  public Optional<SteerPutEvent> putEvent() {
    return Optional.of(SteerPutEvent.PASSIVE_MOT_TRQ_0);
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }
}
