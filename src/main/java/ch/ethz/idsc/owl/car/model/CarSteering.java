// code by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;

public enum CarSteering {
  FRONT(1), // Ackermann
  FRONT_PARALLEL(1), // simple, only recommended for tests
  REAR(1), // Ackermann
  BOTH(.5), // Ackermann
  ;
  // ---
  private CarSteering(double factor) {
    this.factor = DoubleScalar.of(factor);
  }

  public final Scalar factor;
}
