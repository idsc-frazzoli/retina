// code by jph
package ch.ethz.idsc.owl.car.math;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface SlipInterface {
  /** @return vector with 2 entries that measure the degree of friction (in tire coordinates) */
  Tensor slip();
}
