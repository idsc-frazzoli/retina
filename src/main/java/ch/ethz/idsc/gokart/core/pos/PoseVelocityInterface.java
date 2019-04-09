// code by mh
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface PoseVelocityInterface extends GokartPoseInterface {
  /** @return velocity vector of length 2 in local frame {dotx[m*s^-1], doty[m*s^-1]}
   * {forward speed, side speed (to the left)} measured at the center of the rear-axle */
  Tensor getVelocityXY();

  /** @return angular velocity[s^-1] */
  Scalar getGyroZ();
}
