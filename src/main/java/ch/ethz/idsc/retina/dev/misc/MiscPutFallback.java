// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.util.Optional;

import ch.ethz.idsc.owl.math.state.ProviderRank;

/* package */ enum MiscPutFallback implements MiscPutProvider {
  INSTANCE;
  // ---
  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override // from PutProvider
  public Optional<MiscPutEvent> putEvent() {
    return Optional.of(MiscPutEvent.FALLBACK);
  }
}
