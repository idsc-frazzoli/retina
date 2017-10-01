// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

/** when no entity controls the break, then
 * the break is commanded to be in off-mode */
/* package */ enum LinmotPutFallback implements LinmotPutProvider {
  INSTANCE;
  // ---
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    return Optional.of(LinmotPutHelper.OFF_MODE_EVENT);
  }
}
