// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface TorqueVectoringInterface {
  /** @param angularSlip
   * @param wantedPower unitless ideally in the interval [-1, 1]
   * @return vector of the form {powerLeft, powerRight} where both
   * powerLeft and powerRight are guaranteed to be in the interval [-1, 1] */
  Tensor powers(AngularSlip angularSlip, Scalar wantedPower);

  /** @param wantedZTorque TODO MH state unit
   * @param realRotation s^-1
   * @return quantity with unit same as wantedZTorque */
  Scalar wantedZTorque(Scalar wantedZTorque, Scalar realRotation);
}
