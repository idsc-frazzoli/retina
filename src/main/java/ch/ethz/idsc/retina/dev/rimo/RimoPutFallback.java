// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

public enum RimoPutFallback implements RimoPutProvider {
  INSTANCE;
  // ---
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override
  public Optional<RimoPutEvent> pollPutEvent() {
    return Optional.of(new RimoPutEvent( //
        new RimoPutTire(RimoPutTire.OPERATION, (short) 0), //
        new RimoPutTire(RimoPutTire.OPERATION, (short) 0)));
  }
}
