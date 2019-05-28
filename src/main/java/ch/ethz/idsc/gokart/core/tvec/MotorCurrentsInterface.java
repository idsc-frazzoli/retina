// code by mh, jph
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.owl.car.math.AngularSlip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface MotorCurrentsInterface {
  /** get torque vectoring motor currents corresponding to the wanted rotation speed
   * (this can also be used externally!)
   * 
   * @param angularSlip
   * @param wantedAcceleration [m*s^-2]
   * @return the motor currents [ARMS] */
  Tensor fromAcceleration(AngularSlip angularSlip, Scalar wantedAcceleration);
}
