// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.tensor.Scalar;

/* package */ interface RimoRateController {
  /** @param vel_error with unit "s^-1"
   * @return value with unit "ARMS" */
  Scalar iterate(Scalar vel_error);

  /** set the current wheel rate
   * 
   * @param vel_avg current wheel rate unit "s^-1" */
  void setWheelRate(Scalar vel_avg);
}
