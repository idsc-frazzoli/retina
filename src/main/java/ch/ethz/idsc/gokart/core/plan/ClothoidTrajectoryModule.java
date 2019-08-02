// code by ynager, jph, gjoel
package ch.ethz.idsc.gokart.core.plan;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitModule;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.mod.PlannerPublish;
import ch.ethz.idsc.owl.glc.adapter.GlcTrajectories;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.TrajectorySample;

// TODO make configurable as parameter
public class ClothoidTrajectoryModule extends GokartTrajectoryModule {
  public ClothoidTrajectoryModule() {
    this(TrajectoryConfig.GLOBAL);
  }

  /* package */ ClothoidTrajectoryModule(TrajectoryConfig trajectoryConfig) {
    super(trajectoryConfig, new CurveClothoidPursuitModule(ClothoidPursuitConfig.GLOBAL));
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) { // goal reached
      List<TrajectorySample> tail = //
          GlcTrajectories.detailedTrajectoryTo(trajectoryPlanner.getStateIntegrator(), optional.get());
      trajectory = Trajectories.glue(head, tail);
      PlannerPublish.trajectory(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME, trajectory);
    } else {
      // failure to reach goal
      // ante 20181025: previous trajectory was cleared
      // post 20181025: keep old trajectory
      System.err.println("use old trajectory");
    }
    if (Objects.nonNull(trajectory))
      curvePursuitModule.setTrajectory(trajectory);
  }
}
