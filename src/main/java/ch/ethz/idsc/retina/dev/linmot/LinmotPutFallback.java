// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

/** when no entity controls the break, then
 * the break is commanded to be in off-mode */
/* package */ enum LinmotPutFallback implements LinmotPutProvider {
  INSTANCE;
  private final LinmotPutEvent lpe;

  private LinmotPutFallback() {
    lpe = new LinmotPutEvent( //
        LinmotPutHelper.CMD_OPERATION, LinmotPutHelper.MC_POSITION);
    lpe.target_position = -48;
    lpe.max_velocity = 1000;
    lpe.acceleration = 500;
    lpe.deceleration = 500;
  }

  // ---
  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    // return Optional.of(LinmotPutHelper.OFF_MODE_EVENT);
    return Optional.of(lpe);
  }
}
