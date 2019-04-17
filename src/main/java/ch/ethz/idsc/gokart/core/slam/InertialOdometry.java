// code by mh, jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.PoseVelocityInterface;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Mod;

/** integrated, a priori unfiltered, PoseVelocityInterface
 * 
 * the implementation uses the following sensor information:
 * 1) accelerationXY and gyroZ
 * 2) pose and velocity estimates
 * 
 * the implementation is independent of the sources of the values */
/* package */ class InertialOdometry implements PoseVelocityInterface {
  private static final Mod MOD_DISTANCE = Mod.function(Pi.TWO, Pi.VALUE.negate());
  private static final Tensor VELOCITY_ZERO = Tensors.of(Quantity.of(0.0, SI.VELOCITY), Quantity.of(0.0, SI.VELOCITY));
  // ---
  private Tensor pose = GokartPoseEvents.motionlessUninitialized().getPose();
  private Tensor localVelocityXY = VELOCITY_ZERO;
  private Scalar gyroZ = Quantity.of(0.0, SI.PER_SECOND);

  /** override stored pose to given pose
   * 
   * @param pose {x[m], y[m], angle[]} */
  public final synchronized void resetPose(Tensor pose) {
    this.pose = pose.copy();
  }

  /** sets velocity to {0[m*s^-1], 0[m*s^-1]} */
  final synchronized void resetVelocity() {
    localVelocityXY = VELOCITY_ZERO;
  }

  /** take new acceleration measurement into account
   * 
   * @param local_accXY {x[m*s^-2], y[m*s^-2]}
   * @param gyroZ with unit [s^-1]
   * @param deltaT [s] */
  /* package */ final synchronized void integrateImu(Tensor local_accXY, Scalar gyroZ, Scalar deltaT) {
    // transform old velocity to new frame of reference (compensate for rotation), then add integrated acceleration
    localVelocityXY = RotationMatrix.of(gyroZ.negate().multiply(deltaT)).dot(localVelocityXY) //
        .add(local_accXY.multiply(deltaT));
    // update gyro
    this.gyroZ = gyroZ;
    // integrate pose
    pose = Se2CoveringIntegrator.INSTANCE.spin(pose, getVelocity().multiply(deltaT));
    pose.set(MOD_DISTANCE, 2);
  }

  @Override // from PoseVelocityInterface
  public final synchronized Tensor getPose() {
    return pose.copy();
  }

  @Override // from PoseVelocityInterface
  public final Tensor getVelocity() {
    return localVelocityXY.copy().append(gyroZ);
  }

  @Override // from PoseVelocityInterface
  public final Scalar getGyroZ() {
    return gyroZ;
  }

  /** @param velXY {velx[m*s^-1], vely[m*s^-1]}
   * @param scalar in the interval [0, 1] */
  final synchronized void blendVelocity(Tensor velXY, Scalar scalar) {
    localVelocityXY = RnGeodesic.INSTANCE.split(localVelocityXY, velXY, scalar);
  }

  /** @param pose {x[m], y[m], angle[]}
   * @param scalar in the interval [0, 1] */
  final synchronized void blendPose(Tensor pose, Scalar scalar) {
    this.pose = Se2Geodesic.INSTANCE.split(this.pose, pose, scalar);
    this.pose.set(MOD_DISTANCE, 2);
  }
}
