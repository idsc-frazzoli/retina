// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.util.Optional;

import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.tensor.Scalar;

/* package */ class LinmotPressTestLinmot implements LinmotPutProvider {
  private boolean isActive = false;
  private Scalar scalar;

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    return isActive //
        ? Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(scalar))
        : Optional.empty();
  }

  public void startPress(Scalar scalar) {
    this.scalar = scalar;
    isActive = true;
  }

  public void stopPress() {
    isActive = false;
  }
}
