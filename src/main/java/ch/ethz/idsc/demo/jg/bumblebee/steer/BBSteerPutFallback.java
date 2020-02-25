// code by jph, gjoel
package ch.ethz.idsc.demo.jg.bumblebee.steer;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.PutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;

/* package */ enum BBSteerPutFallback implements PutProvider<BBSteerPutEvent> {
  INSTANCE;
  // ---
  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override // from PutProvider
  public Optional<BBSteerPutEvent> putEvent() {
    return Optional.of(BBSteerPutEvent.PASSIVE);
  }
}
