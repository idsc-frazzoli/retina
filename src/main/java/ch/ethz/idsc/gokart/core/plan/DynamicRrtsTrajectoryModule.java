// code by gjoel
package ch.ethz.idsc.gokart.core.plan;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.CurvePursuitModule;
import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.core.track.BSplineTrackLcmClient;
import ch.ethz.idsc.gokart.core.track.BSplineTrackListener;
import ch.ethz.idsc.gokart.core.track.TrackLane;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.owl.math.lane.LaneSegment;
import ch.ethz.idsc.owl.math.lane.StableLanes;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoids;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.RotateLeft;

// TODO make configurable as parameter
public class DynamicRrtsTrajectoryModule extends RrtsTrajectoryModule implements BSplineTrackListener {
  private static final int RESOLUTION = 25;
  // ---
  private final BSplineTrackLcmClient bSplineTrackLcmClient = BSplineTrackLcmClient.closed();
  private Optional<LaneInterface> trackLane = Optional.empty();

  public DynamicRrtsTrajectoryModule(TrajectoryConfig trajectoryConfig, //
      CurvePursuitModule curvePursuitModule, //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery... transitionRegionQueries) {
    super(trajectoryConfig, curvePursuitModule, transitionSpace, transitionRegionQueries);
  }

  @Override // from GokartTrajectoryModule
  public void first() {
    super.first();
    bSplineTrackLcmClient.addListener(this);
    bSplineTrackLcmClient.startSubscriptions();
  }

  @Override // from GokartTrajectoryModule
  public void last() {
    bSplineTrackLcmClient.stopSubscriptions();
    super.last();
  }

  @Override // from RrtsTrajectoryModule
  protected Optional<LaneInterface> laneSegment(Tensor state, Tensor goal) {
    if (/* Objects.nonNull(ModuleAuto.INSTANCE.getInstance(TrackReconModule.class)) && */ trackLane.isPresent())
      return trackLane.map(laneInterface -> LaneSegment.of(laneInterface, state, goal));
    int rootIdx = locate(waypoints, state);
    Tensor shifted = RotateLeft.of(waypoints, rootIdx);
    Tensor segment = shifted.extract(0, locate(shifted, goal) + 1);
    final Scalar r = Magnitude.METER.apply(trajectoryConfig.rrtsLaneWidth);
    return Optional.of(StableLanes.of(segment, Clothoids.CURVE_SUBDIVISION::string, 3, r));
  }

  @Override // from BSplineTrackListener
  public void bSplineTrack(Optional<BSplineTrack> optional) {
    trackLane = optional.map(track -> track.getTrackBoundaries(RESOLUTION)).map(TrackLane::unitless);
  }
}
