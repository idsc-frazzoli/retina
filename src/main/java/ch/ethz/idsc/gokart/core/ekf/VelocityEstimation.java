// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface VelocityEstimation {
  // TODO MH document
  Tensor getVelocity();
}
