// code by mh, jph
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.owl.car.slip.AngularSlip;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface MotorCurrentsInterface {
  /** get torque vectoring motor currents corresponding to the wanted rotation speed
   * 
   * @param angularSlip
   * @param wantedAcceleration [m*s^-2]
   * @return the motor currents [ARMS] */
  Tensor fromAcceleration(AngularSlip angularSlip, Scalar wantedAcceleration);
}
