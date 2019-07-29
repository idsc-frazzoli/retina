// code by mg
package ch.ethz.idsc.retina.app.slam;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
interface GokartPoseUnitlessInterface {
  /** @return {x, y, heading} */
  Tensor getPoseUnitless();
}
