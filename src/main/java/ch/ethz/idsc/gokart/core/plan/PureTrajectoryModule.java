// code by ynager, jph
package ch.ethz.idsc.gokart.core.plan;

import ch.ethz.idsc.gokart.core.pure.CurvePurePursuitModule;
import ch.ethz.idsc.gokart.core.pure.PurePursuitConfig;

public class PureTrajectoryModule extends GlcTrajectoryModule {
  public PureTrajectoryModule() {
    this(TrajectoryConfig.GLOBAL);
  }

  /* package */ PureTrajectoryModule(TrajectoryConfig trajectoryConfig) {
    super(trajectoryConfig, new CurvePurePursuitModule(PurePursuitConfig.GLOBAL));
  }
}
