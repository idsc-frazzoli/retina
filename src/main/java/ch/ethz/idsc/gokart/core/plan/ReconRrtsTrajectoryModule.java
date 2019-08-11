// code by gjoel
package ch.ethz.idsc.gokart.core.plan;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.gokart.core.pure.CurvePursuitModule;
import ch.ethz.idsc.gokart.core.track.TrackLane;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.owl.math.lane.LaneSegment;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.tensor.Tensor;

// TODO make configurable as parameter
public class ReconRrtsTrajectoryModule extends RrtsTrajectoryModule implements MPCBSplineTrackListener {
  private static int RESOLUTION = 25;
  // ---
  private Optional<LaneInterface> lane = Optional.empty();

  public ReconRrtsTrajectoryModule(TrajectoryConfig trajectoryConfig, //
      CurvePursuitModule curvePursuitModule, //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery... transitionRegionQueries) {
    super(trajectoryConfig, curvePursuitModule, transitionSpace, transitionRegionQueries);
  }

  @Override // from RrtsTrajectoryModule
  protected Optional<LaneInterface> laneSegment(Tensor state, Tensor goal) {
    return lane.map(laneInterface -> LaneSegment.of(laneInterface, state, goal));
  }

  @Override // from MPCBSplineTrackListener
  public void mpcBSplineTrack(Optional<MPCBSplineTrack> optional) {
    lane = optional.map(MPCBSplineTrack::bSplineTrack).map(track -> track.getTrackBoundaries(RESOLUTION)).map(TrackLane::unitless);
  }
}
