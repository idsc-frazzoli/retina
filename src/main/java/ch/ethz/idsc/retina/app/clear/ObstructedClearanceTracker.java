// code by jph
package ch.ethz.idsc.retina.app.clear;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class ObstructedClearanceTracker implements ClearanceTracker, Serializable {
  private final Scalar contact;

  public ObstructedClearanceTracker(Scalar contact) {
    this.contact = Objects.requireNonNull(contact);
  }

  // ---
  @Override // from ClearanceTracker
  public boolean isObstructed(Tensor local) {
    return true;
  }

  @Override // from ClearanceTracker
  public Optional<Scalar> contact() {
    return Optional.of(contact);
  }
}
