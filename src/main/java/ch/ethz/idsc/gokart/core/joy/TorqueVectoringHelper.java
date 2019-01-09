// code by mh
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;

enum TorqueVectoringHelper {
  ;
  private static final Scalar MAX = RealScalar.of(+1.0);
  private static final Scalar MIN = RealScalar.of(-1.0);

  /** @param powerLeft unitless
   * @param powerRight unitless
   * @return vector of length 2 with scalars in interval [-1, 1] */
  // TOOD JPH/MH simplify function (see tests)
  static Tensor clip(Scalar powerLeft, Scalar powerRight) {
    // proposed solution:
    // Tensor v = Tensors.of(powerLeft, powerRight);
    // Tensor c = v.map(Clip.absoluteOne());
    // Tensor d = Reverse.of(v.subtract(c));
    // return v.add(d).map(Clip.absoluteOne());
    // ---
    if (Scalars.lessThan(MAX, powerRight)) {
      Scalar overpower = powerRight.subtract(MAX);
      powerRight = MAX;
      powerLeft = powerLeft.add(overpower);
    } else //
    if (Scalars.lessThan(MAX, powerLeft)) {
      Scalar overpower = powerLeft.subtract(MAX);
      powerLeft = MAX;
      powerRight = powerRight.add(overpower);
    } else //
    if (Scalars.lessThan(powerRight, MIN)) {
      Scalar underPower = powerRight.subtract(MIN);
      powerRight = MIN;
      powerLeft = powerLeft.add(underPower);
    } else //
    if (Scalars.lessThan(powerLeft, MIN)) {
      Scalar underPower = powerLeft.subtract(MIN);
      powerLeft = MIN;
      powerRight = powerRight.add(underPower);
    }
    return Tensors.of(powerLeft, powerRight).map(Clip.absoluteOne());
  }
}
