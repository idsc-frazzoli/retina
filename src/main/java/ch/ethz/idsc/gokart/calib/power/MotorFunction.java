// code by mh, jph
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.tensor.Scalar;

/* package */ interface MotorFunction {
  /** @param power with unit "ARMS"
   * @param speed with unit velocity e.g. "m*s^-1"
   * @return estimated acceleration in "m*s^-2" for given power input and state */
  Scalar getAccelerationEstimation(Scalar power, Scalar speed);
}
