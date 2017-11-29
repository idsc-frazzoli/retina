// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

/** when no entity controls the break, then
 * the break is commanded to be
 * in normal operation mode
 * with non-breaking position */
/* package */ enum LinmotPutFallback implements LinmotPutProvider {
  INSTANCE;
  // ---
  private final LinmotPutEvent linmotPutEvent;

  private LinmotPutFallback() {
    linmotPutEvent = new LinmotPutEvent( //
        LinmotPutHelper.CMD_OPERATION, //
        LinmotPutHelper.MC_POSITION);
  }

  // ---
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    // TODO magic const are tested by redundant
    linmotPutEvent.target_position = -48;
    linmotPutEvent.max_velocity = 1000;
    linmotPutEvent.acceleration = 500;
    linmotPutEvent.deceleration = 500;
    return Optional.of(linmotPutEvent);
  }
}
