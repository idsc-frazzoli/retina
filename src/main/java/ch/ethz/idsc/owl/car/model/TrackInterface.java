// code by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ interface TrackInterface {
  /** @param x
   * @return friction coefficient at state x */
  Scalar mu(Tensor x);
}
