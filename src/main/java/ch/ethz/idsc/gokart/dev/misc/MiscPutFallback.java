// code by jph
package ch.ethz.idsc.gokart.dev.misc;

import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.ProviderRank;

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
