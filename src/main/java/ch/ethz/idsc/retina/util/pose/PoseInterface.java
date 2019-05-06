// code by jph
package ch.ethz.idsc.retina.util.pose;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface PoseInterface {
  /** @return {x[m], y[m], angle} */
  Tensor getPose();
}
