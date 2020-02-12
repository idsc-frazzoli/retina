// code by mh, ta
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ abstract class MPCSteering extends MPCControlUpdateCapture {
  /** get the needed steering angle and the change rate of the needed steering angle
   * 
   * @param time with unit [s]
   * @return {desired steering angle [SCE], desired steering angle change rate [SCE*s^-1]},
   * or Optional.empty() if no steering is defined at given time */
  abstract Optional<Tensor> getSteering(Scalar time);
  
  /** get the needed steering torque and the change rate of the needed steering torque
   * 
   * @param time with unit [s]
   * @return {desired steering torque [SCT], desired steering torque change rate [SCT*s^-1]},
   * or Optional.empty() if no steering is defined at given time */
  abstract Optional<Tensor> getSteeringTorque(Scalar time);
  
  /** get the Velocity and pose of the gokart
   * 
   * @param time with unit [s]
   * @return {desired steering torque [SCT], desired steering torque change rate [SCT*s^-1],
   * desired steering angle [SCE], desired steering angle change rate [SCE*s^-1]},
   * or Optional.empty() if no steering is defined at given time */
  abstract Optional<Tensor> getState(Scalar time);
}
