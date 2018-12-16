// code by jph
package ch.ethz.idsc.gokart.dev.rimo;

import ch.ethz.idsc.tensor.Scalar;

interface RimoRateController {
  /** @param vel_error with unit "rad*s^-1"
   * @return value with unit "ARMS" */
  Scalar iterate(final Scalar vel_error);

  /** set the current wheel rate
   * 
   * @param abs_vel current absolute wheel rate unit "rad*s^-1" */
  default void setWheelRate(Scalar abs_vel) {
    // ---
  }
}
