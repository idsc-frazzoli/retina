// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

/* package */ enum MiscPutFallback implements MiscPutProvider {
  INSTANCE;
  // ---
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override
  public Optional<MiscPutEvent> getPutEvent() {
    return Optional.of(new MiscPutEvent());
  }
}
