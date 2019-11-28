// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ abstract class MPCSteering extends MPCControlUpdateCapture {
  /** get the needed steering angle and the change rate of the needed steering angle
   * 
   * @param time with unit [s]
   * @return {wanted steering angle [SCE], wanted steering angle change rate [SCE*s^-1]},
   * or Optional.empty() if no steering is defined at given time */
  abstract Optional<Tensor> getSteering(Scalar time);
  
  Optional<Tensor> getSteeringTorque(Scalar time) {
    System.out.println("This steering has no get torque command implemented");
    return Optional.of(Tensors.of( null,null));
  
  }
}
