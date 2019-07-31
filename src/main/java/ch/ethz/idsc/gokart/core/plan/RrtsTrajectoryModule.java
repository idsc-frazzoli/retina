// code by gjoel
package ch.ethz.idsc.gokart.core.plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.CurvePursuitModule;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.mod.PlannerPublish;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.owl.math.lane.StableLane;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.LaneRrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.RrtsFlowHelper;
import ch.ethz.idsc.owl.rrts.RrtsNodeCollections;
import ch.ethz.idsc.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.TransitionRegionQueryUnion;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionPlanner;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid3;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.RotateLeft;

// TODO make configurable as parameter
public class RrtsTrajectoryModule extends GokartTrajectoryModule<TransitionPlanner> {
  private static final SplitInterface SPLIT_INTERFACE = Clothoid3.INSTANCE;
  private final TransitionSpace transitionSpace;
  private final Scalar resolution = RationalScalar.of(1, 2); // TODO is this related to PARTITIONSCALE?
  private final Collection<TransitionRegionQuery> transitionRegionQueries;

  public RrtsTrajectoryModule(TrajectoryConfig trajectoryConfig, //
      CurvePursuitModule curvePursuitModule, //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery... transitionRegionQueries) {
    super(trajectoryConfig, curvePursuitModule);
    this.transitionSpace = transitionSpace;
    this.transitionRegionQueries = Arrays.asList(transitionRegionQueries);
  }

  @Override // from GokartTrajectoryModule
  protected final TransitionPlanner setupTreePlanner(StateTime root, Tensor goal) {
    int rootIdx = locate(root.state()).orElseThrow(() -> TensorRuntimeException.of(waypoints));
    Tensor segment = RotateLeft.of(waypoints, rootIdx).extract(0, Math.floorMod(locate(goal).get() - rootIdx + 1, waypoints.length()));
    LaneInterface lane = StableLane.of(SPLIT_INTERFACE, segment, goalRadius.Get(0));
    // ---
    List<TransitionRegionQuery> transitionRegionQueries = //
        new ArrayList<>(Collections.singletonList(new SampledTransitionRegionQuery(obstacleMapping(), RealScalar.of(0.05)))); // TODO magic constant
    transitionRegionQueries.addAll(this.transitionRegionQueries);
    TransitionRegionQuery transitionRegionQuery = TransitionRegionQueryUnion.wrap(transitionRegionQueries);
    LaneRrtsPlannerServer laneRrtsPlannerServer = //
        new LaneRrtsPlannerServer(transitionSpace, transitionRegionQuery, resolution, Se2StateSpaceModel.INSTANCE, true) {
      @Override
      protected RrtsNodeCollection rrtsNodeCollection() {
        Tensor lbound = Array.zeros(3);
        Tensor ubound = Tensors.vector(60, 60, 2 * Math.PI);
        return RrtsNodeCollections.clothoid(lbound, ubound); // TODO GJOEL/JPH replace with next owl version
      }

      @Override
      protected Tensor uBetween(StateTime orig, StateTime dest) {
        return RrtsFlowHelper.U_SE2.apply(orig, dest);
      }
    };
    laneRrtsPlannerServer.setState(root);
    laneRrtsPlannerServer.setGoal(goal);
    laneRrtsPlannerServer.accept(lane);
    return laneRrtsPlannerServer;
  }

  @Override // from GokartTrajectoryModule
  protected void expandResult(List<TrajectorySample> head, TransitionPlanner transitionPlanner) {
    Optional<List<TrajectorySample>> optional = ((LaneRrtsPlannerServer) transitionPlanner).getTrajectory();
    if (optional.isPresent()) {
      trajectory = Trajectories.glue(head, optional.get());
      curvePursuitModule.setTrajectory(optional.get());
      PlannerPublish.publishTrajectory(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME, trajectory);
    } else {
      // failure to reach goal
      // ante 20181025: previous trajectory was cleared
      // post 20181025: keep old trajectory
      System.err.println("use old trajectory");
    }
  }
}
