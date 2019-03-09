// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.util.Objects;

import ch.ethz.idsc.gokart.core.ekf.SimplePositionVelocityModule;
import ch.ethz.idsc.gokart.core.fuse.DavisImuTracker;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

/** implementation of odometry that averages wheel rates for tangent speed
 * and uses low-pass signal of gyro measurement for vehicle turn rate.
 * 
 * This combination was found to be superior over pure wheel odometry. */
/* package */ class GokartGyroPoseOdometry extends GokartPoseOdometry {
  public static GokartPoseOdometry create(Tensor state) {
    return new GokartGyroPoseOdometry(state);
  }

  public static GokartPoseOdometry create() {
    return create(GokartPoseLocal.INSTANCE.getPose());
  }

  // ---
  private final SimplePositionVelocityModule simplePositionVelocityModule = //
      ModuleAuto.INSTANCE.getInstance(SimplePositionVelocityModule.class);

  GokartGyroPoseOdometry(Tensor state) {
    super(state);
    System.out.println("uses vel est " + Objects.nonNull(simplePositionVelocityModule));
  }

  /** .
   * @param speedL with unit "m*s^-1"
   * @param speedR with unit "m*s^-1"
   * @param yHalfWidth "m*rad^-1", hint: use ChassisGeometry.GLOBAL.yTireRear
   * @return */
  @Override
  Flow singleton(Scalar speedL, Scalar speedR, Scalar yHalfWidth) {
    final Scalar rate = Objects.isNull(simplePositionVelocityModule) //
        ? DavisImuTracker.INSTANCE.getGyroZ()
        : simplePositionVelocityModule.getGyroVelocity();
    Tensor x = Objects.isNull(simplePositionVelocityModule) //
        ? Tensors.of(speedL.add(speedR).multiply(HALF), RealScalar.ZERO, rate)
        : simplePositionVelocityModule.getXYVelocity().append(rate);
    return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, N.DOUBLE.of(x));
  }
}
