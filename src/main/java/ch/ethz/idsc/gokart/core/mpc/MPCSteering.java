// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class MPCSteering extends MPCControlUpdateListener implements MPCStateProviderClient {
  // TODO cleanup comments
  /** get the needed steering angle and the change rate of the needed steering angle
   * @param time current time [s]
   * @return {wanted steering angle [SCE], wanted steering angle change rate [SCE/s]} */
  abstract Optional<Tensor> getSteering(Scalar time);
}
