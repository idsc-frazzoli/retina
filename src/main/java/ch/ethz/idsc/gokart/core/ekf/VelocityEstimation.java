// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.tensor.Tensor;

@FunctionalInterface
public interface VelocityEstimation {
  /** @return the velocity vector in gokart frame [dotx[m/s], doty[m/s], angular velocity[1/s]] */
  Tensor getVelocity();
}
