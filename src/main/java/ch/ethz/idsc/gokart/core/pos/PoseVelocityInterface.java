// code by mh, jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public interface PoseVelocityInterface extends GokartPoseInterface {
  /** getVelocity() provides the time derivative of the pose.
   * Since the pose is an element in SE(2), the derivative is
   * a vector in the 3-dimensional Lie-Algebra se(2).
   * 
   * The vector encodes the translational and rotation speed
   * measured in the center of the rear axle.
   * 
   * The tangent vector can transformed to another frame of reference
   * using the adjoint mapping.
   * 
   * measured at the center of the rear-axle:
   * vx == forward speed
   * vy == side speed (to the left)
   * 
   * @return {vx[m*s^-1], vy[m*s^-1], gyroZ[s^-1]} */
  Tensor getVelocity();

  /** @return angular velocity[s^-1] */
  Scalar getGyroZ();
}
