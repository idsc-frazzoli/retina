// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.Tensor;

// TODO JPH rename to PoseInterface
@FunctionalInterface
public interface GokartPoseInterface {
  /** @return {x[m], y[m], angle} */
  Tensor getPose();
}
