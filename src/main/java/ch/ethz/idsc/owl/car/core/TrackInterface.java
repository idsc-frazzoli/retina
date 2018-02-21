// code by jph
package ch.ethz.idsc.owl.car.core;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface TrackInterface {
  /** @param x
   * @return friction coefficient at state x */
  Scalar mu(Tensor x);
}
