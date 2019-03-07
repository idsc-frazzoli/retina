// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;

/* package */ class LinmotPressTestRimo implements RimoPutProvider {
  private boolean isActive = false;

  @Override // from RimoPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.CALIBRATION; // has to trump MANUAL rank
  }

  @Override // from RimoPutProvider
  public Optional<RimoPutEvent> putEvent() {
    return isActive //
        ? Optional.of(RimoPutEvent.PASSIVE) //
        : Optional.empty();
  }

  public void startPress() {
    isActive = true;
  }

  public void stopPress() {
    isActive = false;
  }
}
