// code by ynager, jph, gjoel
package ch.ethz.idsc.gokart.core.plan;

import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitModule;

public class ClothoidTrajectoryModule extends GlcTrajectoryModule {
  public ClothoidTrajectoryModule() {
    this(TrajectoryConfig.GLOBAL);
  }

  /* package */ ClothoidTrajectoryModule(TrajectoryConfig trajectoryConfig) {
    super(trajectoryConfig, new CurveClothoidPursuitModule(ClothoidPursuitConfig.GLOBAL));
  }
}
