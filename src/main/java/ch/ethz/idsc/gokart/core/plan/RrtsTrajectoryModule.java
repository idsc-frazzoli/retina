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
import ch.ethz.idsc.owl.bot.se2.rrts.ClothoidRrtsNdType;
import ch.ethz.idsc.owl.glc.adapter.Trajectories;
import ch.ethz.idsc.owl.math.MinMax;
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
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid3;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.RotateLeft;
import ch.ethz.idsc.tensor.opt.Pi;

// TODO make configurable as parameter
public class RrtsTrajectoryModule extends GokartTrajectoryModule<TransitionPlanner> {
  private static final SplitInterface SPLIT_INTERFACE = Clothoid3.INSTANCE;
  private final TransitionSpace transitionSpace;
  private final Scalar resolution = RealScalar.ONE; // TODO is this related to PARTITIONSCALE?
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
    int rootIdx = locate(waypoints, root.state());
    Tensor shifted = RotateLeft.of(waypoints, rootIdx);
    Tensor segment = shifted.extract(0, locate(shifted, goal) + 1);
    final Scalar r = Magnitude.METER.apply(trajectoryConfig.rrtsLaneWidth);
    LaneInterface lane = StableLane.of(SPLIT_INTERFACE, segment, r);
    // ---
    List<TransitionRegionQuery> transitionRegionQueries = //
        new ArrayList<>(Collections.singletonList(new SampledTransitionRegionQuery(mapping.getMap(), RealScalar.of(0.05)))); // TODO magic constant
    transitionRegionQueries.addAll(this.transitionRegionQueries);
    TransitionRegionQuery transitionRegionQuery = TransitionRegionQueryUnion.wrap(transitionRegionQueries);
    LaneRrtsPlannerServer laneRrtsPlannerServer = //
        new LaneRrtsPlannerServer(transitionSpace, transitionRegionQuery, resolution, Se2StateSpaceModel.INSTANCE, true) {
          @Override
          protected RrtsNodeCollection rrtsNodeCollection() {
            Scalar r_2 = r.multiply(RationalScalar.HALF);
            MinMax minMaxX = MinMax.of(waypoints.get(Tensor.ALL, 0));
            MinMax minMaxY = MinMax.of(waypoints.get(Tensor.ALL, 1));
            Tensor lbounds_ = Tensors.of(minMaxX.min().subtract(r_2), minMaxY.min().subtract(r_2), RealScalar.ZERO);
            Tensor ubounds_ = Tensors.of(minMaxX.max().add(r_2), minMaxY.max().add(r_2), Pi.TWO);
            return new RrtsNodeCollections(ClothoidRrtsNdType.INSTANCE, lbounds_, ubounds_);
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
      PlannerPublish.trajectory(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME, trajectory);
    } else {
      // failure to reach goal
      // ante 20181025: previous trajectory was cleared
      // post 20181025: keep old trajectory
      System.err.println("use old trajectory");
    }
  }
}
