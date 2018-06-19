// code by jph
package ch.ethz.idsc.owl.car.math;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum EmptyClearanceTracker implements ClearanceTracker {
  INSTANCE;
  // ---
  @Override // from ClearanceTracker
  public boolean isObstructed(Tensor local) {
    return false;
  }

  @Override // from ClearanceTracker
  public Optional<Scalar> contact() {
    return Optional.empty();
  }
}
