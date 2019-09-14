// code by gjoel, jph
package ch.ethz.idsc.gokart.core.plan;

import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitModule;

public class DubinsRrtsTrajectoryModule extends DynamicRrtsTrajectoryModule {
  public DubinsRrtsTrajectoryModule(TrajectoryConfig trajectoryConfig) {
    super(trajectoryConfig, //
        new CurveClothoidPursuitModule(ClothoidPursuitConfig.GLOBAL), //
        trajectoryConfig.dubinsTransitionSpace());
  }

  public DubinsRrtsTrajectoryModule() {
    this(TrajectoryConfig.GLOBAL);
  }
}
