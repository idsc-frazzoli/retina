// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.gokart.core.ekf.PositionVelocityEstimation;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringIntegrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Mod;

public class InertialOdometry implements PositionVelocityEstimation {
  private static final Mod MOD_DISTANCE = Mod.function(Pi.TWO, Pi.VALUE.negate());
  // ---
  private final Scalar filter;
  private Tensor pose = GokartPoseHelper.attachUnits(Tensors.vector(0, 0, 0));
  private Tensor localVelocity = Tensors.of(Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.VELOCITY));
  private Scalar vmu931_gyroZ = Quantity.of(0, SI.PER_SECOND);

  public InertialOdometry(Scalar filter) {
    this.filter = filter;
  }

  /** @param pose {x[m], y[m], angle[]} */
  public void resetPose(Tensor pose) {
    this.pose = pose.copy();
  }

  /* package */ void integrateImu(Tensor local_acc, Scalar gyro, Scalar deltaT) {
    vmu931_gyroZ = (Scalar) RnGeodesic.INSTANCE.split(vmu931_gyroZ, gyro, filter);
    // transform old system (compensate for rotation)
    localVelocity = RotationMatrix.of(gyro.negate().multiply(deltaT)).dot(localVelocity).add(local_acc.multiply(deltaT));
    // integrate pose
    pose = Se2CoveringIntegrator.INSTANCE.spin(pose, getVelocity().multiply(deltaT));
    pose.set(MOD_DISTANCE, 2);
  }

  @Override // from PositionVelocityEstimation
  public Tensor getPose() {
    return pose;
  }

  @Override // from PositionVelocityEstimation
  public Tensor getVelocity() {
    return localVelocity.copy().append(vmu931_gyroZ);
  }
}
