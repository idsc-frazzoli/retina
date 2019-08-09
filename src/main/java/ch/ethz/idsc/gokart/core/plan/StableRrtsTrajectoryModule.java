// code by gjoel
package ch.ethz.idsc.gokart.core.plan;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.CurvePursuitModule;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.owl.math.lane.StableLane;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid3;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.RotateLeft;

// TODO make configurable as parameter
public class StableRrtsTrajectoryModule extends RrtsTrajectoryModule {
  private static final SplitInterface SPLIT_INTERFACE = Clothoid3.INSTANCE;

  public StableRrtsTrajectoryModule(TrajectoryConfig trajectoryConfig, //
      CurvePursuitModule curvePursuitModule, //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery... transitionRegionQueries) {
    super(trajectoryConfig, curvePursuitModule, transitionSpace, transitionRegionQueries);
  }

  @Override // from RrtsTrajectoryModule
  protected Optional<LaneInterface> laneSegment(Tensor state, Tensor goal) {
    int rootIdx = locate(waypoints, state);
    Tensor shifted = RotateLeft.of(waypoints, rootIdx);
    Tensor segment = shifted.extract(0, locate(shifted, goal) + 1);
    final Scalar r = Magnitude.METER.apply(trajectoryConfig.rrtsLaneWidth);
    return Optional.of(StableLane.of(SPLIT_INTERFACE, segment, r));
  }
}
