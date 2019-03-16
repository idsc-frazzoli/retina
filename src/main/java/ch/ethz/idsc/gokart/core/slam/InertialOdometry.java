// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.gokart.core.ekf.PositionVelocityEstimation;
import ch.ethz.idsc.gokart.core.ekf.VelocityEstimationConfig;
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
  Tensor filteredPose = GokartPoseHelper.attachUnits(Tensors.vector(0, 0, 0));
  Tensor local_filteredVelocity = Tensors.of(Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.VELOCITY));
  Scalar vmu931_gyroZ = Quantity.of(0, SI.PER_SECOND);

  /* package */ synchronized void integrateImu(Tensor local_acc, Scalar gyro, Scalar deltaT) {
    vmu931_gyroZ = (Scalar) RnGeodesic.INSTANCE.split(vmu931_gyroZ, gyro, VelocityEstimationConfig.GLOBAL.rotFilter);
    // transform old system (compensate for rotation)
    local_filteredVelocity = RotationMatrix.of(gyro.negate().multiply(deltaT)).dot(local_filteredVelocity).add(local_acc.multiply(deltaT));
    // integrate pose
    filteredPose = Se2CoveringIntegrator.INSTANCE.spin(filteredPose, getVelocity().multiply(deltaT));
    filteredPose.set(MOD_DISTANCE, 2);
    // System.out.println(filteredPose.map(Round._4));
    // setPose(filteredPose, RealScalar.ONE);
  }

  @Override // from PositionVelocityEstimation
  public Tensor getPose() {
    return filteredPose;
  }

  @Override // from PositionVelocityEstimation
  public Tensor getVelocity() {
    return local_filteredVelocity.copy().append(vmu931_gyroZ);
  }
}
