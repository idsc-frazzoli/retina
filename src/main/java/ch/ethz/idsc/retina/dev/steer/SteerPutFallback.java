// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

/* package */ enum SteerPutFallback implements SteerPutProvider {
  INSTANCE;
  // ---
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    return Optional.of(new SteerPutEvent(SteerPutEvent.CMD_OFF, 0));
  }
}
