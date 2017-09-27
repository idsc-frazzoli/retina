// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

/* package */ enum RimoPutFallback implements RimoPutProvider {
  INSTANCE;
  // ---
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    return Optional.of(RimoPutEvent.STOP);
  }
}
