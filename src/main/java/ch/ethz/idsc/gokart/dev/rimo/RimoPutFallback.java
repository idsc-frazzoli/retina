// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.ProviderRank;

/* package */ enum RimoPutFallback implements RimoPutProvider {
  INSTANCE;
  // ---
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override // from PutProvider
  public Optional<RimoPutEvent> putEvent() {
    return Optional.of(RimoPutEvent.PASSIVE);
  }
}
