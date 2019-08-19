// code by gjoel
package ch.ethz.idsc.gokart.core.plan;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.gokart.core.pure.CurvePursuitModule;
import ch.ethz.idsc.gokart.core.track.BSplineTrack;
import ch.ethz.idsc.gokart.core.track.TrackLane;
import ch.ethz.idsc.gokart.core.track.TrackReconModule;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.owl.math.lane.LaneSegment;
import ch.ethz.idsc.owl.math.lane.StableLane;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid3;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.RotateLeft;

// TODO make configurable as parameter
public class DynamicRrtsTrajectoryModule extends RrtsTrajectoryModule implements MPCBSplineTrackListener {
  private static final int RESOLUTION = 25;
  // ---
  private Optional<LaneInterface> trackLane = Optional.empty();
  private TrackReconModule trackReconModule = null;

  public DynamicRrtsTrajectoryModule(TrajectoryConfig trajectoryConfig, //
      CurvePursuitModule curvePursuitModule, //
      TransitionSpace transitionSpace, //
      TransitionRegionQuery... transitionRegionQueries) {
    super(trajectoryConfig, curvePursuitModule, transitionSpace, transitionRegionQueries);
  }

  @Override // from GokartTrajectoryModule
  public void last() {
    if (Objects.nonNull(trackReconModule))
      trackReconModule.listenersRemove(this);
    super.last();
  }

  @Override // from GokartTrajectoryModule
  protected synchronized void runAlgo() {
    TrackReconModule trackReconModule = ModuleAuto.INSTANCE.getInstance(TrackReconModule.class);
    if (this.trackReconModule != trackReconModule) {
      this.trackReconModule = trackReconModule;
      if (Objects.nonNull(this.trackReconModule))
        this.trackReconModule.listenersAdd(this);
      else
        trackLane = Optional.empty();
    }
    super.runAlgo();
  }

  @Override // from RrtsTrajectoryModule
  protected Optional<LaneInterface> laneSegment(Tensor state, Tensor goal) {
    if (Objects.nonNull(trackReconModule) && trackLane.isPresent())
      return trackLane.map(laneInterface -> LaneSegment.of(laneInterface, state, goal));
    int rootIdx = locate(waypoints, state);
    Tensor shifted = RotateLeft.of(waypoints, rootIdx);
    Tensor segment = shifted.extract(0, locate(shifted, goal) + 1);
    final Scalar r = Magnitude.METER.apply(trajectoryConfig.rrtsLaneWidth);
    return Optional.of(StableLane.of(segment, Clothoid3.CURVE_SUBDIVISION::string, 3, r));
  }

  @Override // from MPCBSplineTrackListener
  public void mpcBSplineTrack(Optional<MPCBSplineTrack> optional) {
    trackLane = optional.map(MPCBSplineTrack::bSplineTrack).filter(BSplineTrack::isClosed) //
        .map(track -> track.getTrackBoundaries(RESOLUTION)).map(TrackLane::unitless);
  }
}
