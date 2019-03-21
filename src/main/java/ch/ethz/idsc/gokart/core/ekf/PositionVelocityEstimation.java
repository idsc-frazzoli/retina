// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.tensor.Tensor;

public interface PositionVelocityEstimation {
  /** @return the position vector {x[m], y[m], orientation[]} */
  Tensor getPose();

  /** @return the velocity vector in local frame {dotx[m*s^-1], doty[m*s^-1], angular velocity[s^-1]} */
  Tensor getVelocity();
}
