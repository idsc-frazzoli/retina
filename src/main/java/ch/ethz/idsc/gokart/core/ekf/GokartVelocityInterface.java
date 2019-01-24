// code by jph
package ch.ethz.idsc.gokart.core.ekf;

import ch.ethz.idsc.tensor.Tensor;

public interface GokartVelocityInterface {
  /** @return {dotX[m/s], dotY[m/s], angular velocity[1/s]} */
  Tensor getVelocity();
}
