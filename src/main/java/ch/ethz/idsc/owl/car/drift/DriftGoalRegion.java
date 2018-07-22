// code by edo
package ch.ethz.idsc.owl.car.drift;

import java.io.Serializable;

import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

class DriftGoalRegion implements Region<Tensor>, Serializable {
  private final Tensor goalState;
  private final Tensor tolerance;

  public DriftGoalRegion(Tensor goalState, Tensor tolerance) {
    this.goalState = goalState;
    this.tolerance = tolerance.map(Scalar::reciprocal);
  }

  @Override // from Region
  public boolean isMember(Tensor tensor) {
    Tensor error = tensor.subtract(goalState).pmul(tolerance);
    Scalar norm = Norm.INFINITY.ofVector(error);
    return Scalars.lessThan(norm, RealScalar.ONE);
  }
}
