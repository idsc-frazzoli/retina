// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.ProviderRank;

/** when no other {@link LinmotPutProvider} controls the break, then
 * the break is commanded to be in operation mode with non-breaking position */
/* package */ enum LinmotPutFallback implements LinmotPutProvider {
  INSTANCE;
  // ---
  @Override // from LinmotPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override // from LinmotPutProvider
  public Optional<LinmotPutEvent> putEvent() {
    return Optional.of(LinmotPutOperation.INSTANCE.fallback());
  }
}
