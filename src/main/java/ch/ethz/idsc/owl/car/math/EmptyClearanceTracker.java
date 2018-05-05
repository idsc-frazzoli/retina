// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.tensor.Tensor;

public enum EmptyClearanceTracker implements ClearanceTracker {
  INSTANCE;
  // ---
  @Override // from ClearanceTracker
  public boolean isObstructed(Tensor local) {
    return false;
  }
}
