// code by gjoel
package ch.ethz.idsc.gokart.core.plan;

import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitModule;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidCurvatureQuery;
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidTransitionSpace;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.sca.Clips;

public class ClothoidRrtsTrajectoryModule extends DynamicRrtsTrajectoryModule {
  public ClothoidRrtsTrajectoryModule() {
    this(TrajectoryConfig.GLOBAL);
  }

  public ClothoidRrtsTrajectoryModule(TrajectoryConfig trajectoryConfig) {
    super(trajectoryConfig, //
        new CurveClothoidPursuitModule(ClothoidPursuitConfig.GLOBAL), //
        ClothoidTransitionSpace.INSTANCE, //
        new ClothoidCurvatureQuery(Clips.absolute(Magnitude.PER_METER.apply(ClothoidPursuitConfig.GLOBAL.turningRatioMax))));
  }
}
