// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.ProviderRank;

/* package */ enum SteerPutFallback implements SteerPutProvider {
  INSTANCE;
  // ---
  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override // from PutProvider
  public Optional<SteerPutEvent> putEvent() {
    return Optional.of(SteerPutEvent.PASSIVE_MOT_TRQ_0);
  }
}
