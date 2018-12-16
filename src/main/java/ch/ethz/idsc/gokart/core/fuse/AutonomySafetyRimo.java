// code by mh
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;

/* package */ class AutonomySafetyRimo extends AutonomySafetyBase<RimoPutEvent> {
  public AutonomySafetyRimo(Supplier<Boolean> supplier) {
    super(supplier);
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    return supplier.get() //
        ? Optional.empty()
        : StaticHelper.OPTIONAL_RIMO_PASSIVE;
  }
}
