// code by jph
package ch.ethz.idsc.gokart.offline.video;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface TireConfiguration {
  /** @return radius of tire effective for odometry with unit [m] */
  Scalar radius();

  /** @return half width with unit [m] */
  Scalar halfWidth();

  Tensor footprint();
}
