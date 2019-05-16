// code by ynager and jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.mod.PlannerPublish;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.TrajectorySample;

public class PureTrajectoryModule extends GokartTrajectoryModule {
  public PureTrajectoryModule() {
    this(TrajectoryConfig.GLOBAL);
  }

  /* package */ PureTrajectoryModule(TrajectoryConfig trajectoryConfig) {
    super(trajectoryConfig, new CurvePurePursuitModule(PursuitConfig.GLOBAL));
  }

  @Override // from GokartTrajectoryModule
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) { // goal reached
      List<TrajectorySample> tail = //
          GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
      trajectory = Trajectories.glue(head, tail);
      curvePursuitModule.setTrajectory(trajectory);
      PlannerPublish.publishTrajectory(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME, trajectory);
    } else {
      // failure to reach goal
      // ante 20181025: previous trajectory was cleared
      // post 20181025: keep old trajectory
      System.err.println("use old trajectory");
    }
  }
}
