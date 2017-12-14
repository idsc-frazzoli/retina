// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;

/** the steering battery is charged from time to time.
 * during charing, the steering motor should be passive.
 * otherwise, the steering battery may overcharge. */
public enum LinmotFireFighter implements LinmotGetListener, LinmotPutProvider {
  INSTANCE;
  // ---
  private boolean isVeryHot = true;

  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    isVeryHot = !LinmotConfig.GLOBAL.isTemperatureHardwareSafe(linmotGetEvent);
  }

  /***************************************************/
  @Override // from LinmotPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.HARDWARE;
  }

  @Override // from LinmotPutProvider
  public Optional<LinmotPutEvent> putEvent() {
    return Optional.ofNullable(isVeryHot ? LinmotPutHelper.FALLBACK_OPERATION : null);
  }
}
