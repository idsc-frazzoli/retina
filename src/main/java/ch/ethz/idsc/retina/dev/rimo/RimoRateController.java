// code by jph
package ch.ethz.idsc.retina.dev.rimo;

import ch.ethz.idsc.tensor.Scalar;

interface RimoRateController {
  /** @param vel_error with unit "rad*s^-1"
   * @return value with unit "ARMS" */
  Scalar iterate(final Scalar vel_error);
}
