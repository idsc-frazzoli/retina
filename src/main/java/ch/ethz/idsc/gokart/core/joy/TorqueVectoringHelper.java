// code by mh
package ch.ethz.idsc.gokart.core.joy;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Ramp;

/* package */ enum TorqueVectoringHelper {
  ;
  /** @param powerLeft unitless
   * @param powerRight unitless
   * @return vector of length 2 with scalars in interval [-1, 1] */
  static Tensor clip(Scalar powerLeft, Scalar powerRight) {
    // proposed solution:
    Scalar oL = Ramp.FUNCTION.apply(powerLeft.subtract(RealScalar.ONE));
    Scalar uL = Ramp.FUNCTION.apply(powerLeft.negate().subtract(RealScalar.ONE));
    Scalar oR = Ramp.FUNCTION.apply(powerRight.subtract(RealScalar.ONE));
    Scalar uR = Ramp.FUNCTION.apply(powerRight.negate().subtract(RealScalar.ONE));
    Scalar d1 = Max.of(oL, uR).negate();
    Scalar d2 = Max.of(oR, uL);
    Scalar d = d1.add(d2);
    return Tensors.of(powerLeft.add(d), powerRight.subtract(d)).map(Clip.absoluteOne());
  }
}
