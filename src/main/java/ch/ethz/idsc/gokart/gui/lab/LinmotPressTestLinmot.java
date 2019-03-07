// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.tensor.Scalar;

/* package */ class LinmotPressTestLinmot implements LinmotPutProvider {
  private boolean isActive = false;
  private Scalar scalar;
  private Boolean turnOff = false;

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    if (turnOff) {
      return isActive//
          ? Optional.of(LinmotPutOperation.INSTANCE.turnOff())
          : Optional.empty();
    }
    return isActive //
        ? Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(scalar))
        : Optional.empty();
  }

  public void startPress(Scalar scalar) {
    this.scalar = scalar;
    isActive = true;
    turnOff = false;
  }

  public void startTurnOff() {
    isActive = true;
    turnOff = true;
  }

  public void stopTurnOff() {
    isActive = false;
    turnOff = false;
  }

  public void stopPress() {
    isActive = false;
  }
}
