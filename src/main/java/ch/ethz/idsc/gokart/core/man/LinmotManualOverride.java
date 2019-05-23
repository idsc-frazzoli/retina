// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.tensor.RealScalar;

public final class LinmotManualOverride implements LinmotPutProvider {
  @Override
  public Optional<LinmotPutEvent> putEvent() {
    return Optional.of(LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.ZERO));
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.MANUAL;
  }
}
