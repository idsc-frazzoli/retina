// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

/* package */ enum LinmotPutFallback implements LinmotPutProvider {
  INSTANCE;
  // ---
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    // TODO NRJ check if this is good choice
    return Optional.of(new LinmotPutEvent( //
        LinmotPutHelper.CMD_OFF_MODE, //
        LinmotPutHelper.MC_ZEROS));
  }
}
