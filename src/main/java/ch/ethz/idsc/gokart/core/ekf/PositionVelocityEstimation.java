// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.tensor.Tensor;

public interface PositionVelocityEstimation {
  /** @return the position vector [x[m], y[m], orientation[1]] */
  Tensor getPose();

  /** @return the velocity vector in gokart frame [dotx[m/s], doty[m/s], angular velocity[1/s]] */
  Tensor getVelocity();
}
