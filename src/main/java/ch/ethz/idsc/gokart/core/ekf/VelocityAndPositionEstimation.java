// code by mh
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.tensor.Tensor;

public interface VelocityAndPositionEstimation {
  /** @return the velocity vector in gokart frame [dotx[m/s], doty[m/s], angular velocity[1/s]] */
  Tensor getVelocity();

  /** @return the position vector [X[m], X[m], orientation[1]] */
  Tensor getPosition();
}
