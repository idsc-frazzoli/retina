// code by mh
package ch.ethz.idsc.gokart.core.fuse;

import java.util.Optional;
import java.util.function.Supplier;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;

/* package */ class AutonomySafetySteer extends AutonomySafetyBase<SteerPutEvent> {
  public AutonomySafetySteer(Supplier<Boolean> supplier) {
    super(supplier);
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    return supplier.get() //
        ? Optional.empty()
        : Optional.of(SteerPutEvent.PASSIVE_MOT_TRQ_0);
  }
}
