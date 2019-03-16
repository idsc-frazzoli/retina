// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.gokart.core.ekf.PositionVelocityEstimation;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Mod;

/** integrated unfiltered */
public class InertialOdometry implements PositionVelocityEstimation {
  private static final Mod MOD_DISTANCE = Mod.function(Pi.TWO, Pi.VALUE.negate());
  private static final Scalar MIN_DRIFT_VELOCITY = Quantity.of(0.1, SI.VELOCITY);
  // ---
  private Tensor pose = GokartPoseHelper.attachUnits(Tensors.vector(0, 0, 0));
  private Tensor localVelocity = Tensors.of(Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.VELOCITY));
  private Scalar gyroZ = Quantity.of(0, SI.PER_SECOND);

  /** @param pose {x[m], y[m], angle[]} */
  public synchronized void resetPose(Tensor pose) {
    this.pose = pose.copy();
  }

  /** take new acceleration measurement into account
   * 
   * @param local_acc {x[m*s^-2], y[m*s^-2]}
   * @param gyro with unit [s^-1]
   * @param deltaT [s] */
  /* package */ synchronized void integrateImu(Tensor local_acc, Scalar gyro, Scalar deltaT) {
    // transform old system (compensate for rotation)
    localVelocity = RotationMatrix.of(gyro.negate().multiply(deltaT)).dot(localVelocity) //
        .add(local_acc.multiply(deltaT));
    // update gyro
    this.gyroZ = gyro;
    // integrate pose
    pose = Se2CoveringIntegrator.INSTANCE.spin(pose, getVelocity().multiply(deltaT));
    pose.set(MOD_DISTANCE, 2);
  }

  @Override // from PositionVelocityEstimation
  public synchronized Tensor getPose() {
    return pose.copy();
  }

  @Override // from PositionVelocityEstimation
  public synchronized Tensor getVelocity() {
    return localVelocity.copy().append(gyroZ);
  }

  public synchronized Scalar getDrift() {
    return Scalars.lessThan(localVelocity.Get(0).abs(), MIN_DRIFT_VELOCITY) //
        ? RealScalar.ZERO
        : localVelocity.Get(1).divide(localVelocity.Get(0));
  }
}
