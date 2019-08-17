// code by mh, jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseVelocityInterface;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Integrator;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;

/** integrated, a priori unfiltered, PoseVelocityInterface
 * 
 * the implementation uses the following sensor information:
 * 1) accelerationXY and gyroZ
 * 2) pose and velocity estimates
 * 
 * the implementation is independent of the sources of the values */
/* package */ class InertialOdometry implements PoseVelocityInterface {
  private static final Tensor VELOCITY_ZERO = Tensors.of(Quantity.of(0.0, SI.VELOCITY), Quantity.of(0.0, SI.VELOCITY));
  // ---
  private Tensor pose = GokartPoseEvents.motionlessUninitialized().getPose();
  private Tensor localVelocityXY = VELOCITY_ZERO;
  private Scalar gyroZ = Quantity.of(0.0, SI.PER_SECOND);

  /** override stored pose to given pose
   * 
   * @param pose {x[m], y[m], angle[]} */
  public final synchronized void resetPose(Tensor pose) {
    Magnitude.METER.apply(pose.Get(0));
    Magnitude.METER.apply(pose.Get(1));
    Magnitude.ONE.apply(pose.Get(2));
    this.pose = VectorQ.requireLength(pose, 3).copy();
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
    pose = Se2Integrator.INSTANCE.spin(pose, getVelocity().multiply(deltaT));
  }

  @Override // from PoseVelocityInterface
  public final synchronized Tensor getPose() {
    return pose.copy();
  }

  @Override // from PoseVelocityInterface
  public final Tensor getVelocity() {
    return localVelocityXY.copy().append(gyroZ);
  }

  /** function is not member of PoseVelocityInterface
   * this design is deliberate */
  /* package */ final Tensor velocityXY() {
    return localVelocityXY.copy();
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
    this.pose.set(So2.MOD, 2);
  }
}
