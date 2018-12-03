// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.gokart.core.fuse.DavisImuTracker;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

public class GokartGyroPoseOdometry extends GokartPoseOdometry {
  public static GokartGyroPoseOdometry create(Tensor state) {
    return new GokartGyroPoseOdometry(state);
  }

  public static GokartGyroPoseOdometry create() {
    return create(GokartPoseLocal.INSTANCE.getPose());
  }

  // ---
  GokartGyroPoseOdometry(Tensor state) {
    super(state);
  }

  /* package */ @Override
  synchronized void step(Tensor angularRate_Y_pair) {
    // rad 0.14, ytir = 0.65 very good rotation tracking! but speed not accurate
    // rad 0.12, ytir = 0.54 good speed tracking, rotation ok
    Scalar radius = ChassisGeometry.GLOBAL.tireRadiusRear;
    // radius = Quantity.of(0.120, "m*rad^-1");
    Tensor speed_pair = angularRate_Y_pair.multiply(radius); // [rad*s^-1] * [m*rad^-1] == [m*s^-1]
    Scalar yTireRear = ChassisGeometry.GLOBAL.yTireRear;
    // yTireRear = Quantity.of(0.54, "m");
    Flow flow = singleton(speed_pair.Get(0), speed_pair.Get(1), yTireRear);
    state = Se2CarIntegrator.INSTANCE.step(flow, state, dt);
  }

  /** .
   * @param speedL with unit "m*s^-1"
   * @param speedR with unit "m*s^-1"
   * @param yHalfWidth "m*rad^-1", hint: use ChassisGeometry.GLOBAL.yTireRear
   * @return */
  static Flow singleton(Scalar speedL, Scalar speedR, Scalar yHalfWidth) {
    Scalar speed = speedL.add(speedR).multiply(HALF);
    Scalar rate = DavisImuTracker.INSTANCE.getGyroZ();
    return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, //
        N.DOUBLE.of(Tensors.of(speed, RealScalar.ZERO, rate)));
  }
}
