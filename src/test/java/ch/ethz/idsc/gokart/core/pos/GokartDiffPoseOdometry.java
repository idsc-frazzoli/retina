// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.N;

/** implementation of odometry based entirely on wheel rates.
 * 
 * The integration is inaccurate when slip occurs.
 * 
 * The preferred odometry is {@link GokartGyroPoseOdometry}. */
/* package */ class GokartDiffPoseOdometry extends GokartPoseOdometry {
  public static GokartPoseOdometry create(Tensor state) {
    return new GokartDiffPoseOdometry(state);
  }

  public static GokartPoseOdometry create() {
    return create(GokartPoseLocal.INSTANCE.getPose());
  }

  // ---
  GokartDiffPoseOdometry(Tensor state) {
    super(state);
  }

  @Override
  Flow singleton(Scalar speedL, Scalar speedR, Scalar yHalfWidth) {
    Scalar speed = speedL.add(speedR).multiply(HALF);
    Scalar rate = speedR.subtract(speedL).multiply(HALF).divide(yHalfWidth);
    return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, //
        N.DOUBLE.of(Tensors.of(speed, RealScalar.ZERO, rate)));
  }
}
