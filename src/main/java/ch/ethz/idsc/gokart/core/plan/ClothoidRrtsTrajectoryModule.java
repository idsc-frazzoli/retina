// code by gjoel
package ch.ethz.idsc.gokart.core.plan;

import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitModule;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.ethz.idsc.owl.bot.se2.rrts.TransitionCurvatureQuery;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;

public class ClothoidRrtsTrajectoryModule extends StableRrtsTrajectoryModule {
  private static final Scalar TURNING_RATIO_MAX = Magnitude.PER_METER.apply(ClothoidPursuitConfig.GLOBAL.turningRatioMax);

  public ClothoidRrtsTrajectoryModule() {
    this(TrajectoryConfig.GLOBAL);
  }

  public ClothoidRrtsTrajectoryModule(TrajectoryConfig trajectoryConfig) {
    super(trajectoryConfig, //
        new CurveClothoidPursuitModule(ClothoidPursuitConfig.GLOBAL), //
        ClothoidTransitionSpace.INSTANCE, //
        new TransitionCurvatureQuery(TURNING_RATIO_MAX));
  }
}
