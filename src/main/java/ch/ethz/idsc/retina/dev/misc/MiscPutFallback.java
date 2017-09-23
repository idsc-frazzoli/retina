// code by jph
package ch.ethz.idsc.retina.dev.misc;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

public enum MiscPutFallback implements MiscPutProvider {
  INSTANCE;
  // ---
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override
  public Optional<MiscPutEvent> pollPutEvent() {
    return Optional.of(new MiscPutEvent());
  }
}
