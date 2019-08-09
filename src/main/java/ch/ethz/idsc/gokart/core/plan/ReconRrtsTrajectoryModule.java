// code by gjoel
package ch.ethz.idsc.gokart.core.plan;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.gokart.core.pure.CurvePursuitModule;
import ch.ethz.idsc.gokart.core.track.TrackLane;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.RotateLeft;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Norm;

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

// TODO JPH/GJOEL remove as this is also in owl
class LaneSegment implements LaneInterface {
  public static LaneInterface of(LaneInterface laneInterface, Tensor start, Tensor end) {
    int fromIdx = ArgMin.of(Tensor.of(laneInterface.midLane().stream().map(start::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
    int toIdx = ArgMin.of(Tensor.of(laneInterface.midLane().stream().map(end::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
    return new LaneSegment(laneInterface, fromIdx, toIdx);
  }

  private final LaneInterface laneInterface;
  private final int fromIdx;
  private final int toIdx;

  private LaneSegment(LaneInterface laneInterface, int fromIdx, int toIdx) {
    this.laneInterface = laneInterface;
    this.fromIdx = fromIdx;
    this.toIdx = toIdx;
  }

  @Override // from LaneInterface
  public Tensor controlPoints() {
    int fromIdx = ArgMin.of(Tensor.of(laneInterface.controlPoints().stream() //
        .map(laneInterface.midLane().get(this.fromIdx)::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
    int toIdx = ArgMin.of(Tensor.of(laneInterface.controlPoints().stream() //
        .map(laneInterface.midLane().get(this.toIdx)::subtract).map(Extract2D.FUNCTION).map(Norm._2::ofVector)));
    int idx = Math.floorMod(toIdx - fromIdx, laneInterface.controlPoints().length());
    return RotateLeft.of(laneInterface.controlPoints(), fromIdx).extract(0, idx + 1);
  }

  @Override // from LaneInterface
  public Tensor midLane() {
    return segment(laneInterface.midLane());
  }

  @Override // from LaneInterface
  public Tensor leftBoundary(){
    return segment(laneInterface.leftBoundary());
  }

  @Override // from LaneInterface
  public Tensor rightBoundary() {
    return segment(laneInterface.rightBoundary());
  }

  @Override // from LaneInterface
  public Tensor margins() {
    return segment(laneInterface.margins());
  }

  private Tensor segment(Tensor tensor) {
    int idx = Math.floorMod(toIdx - fromIdx, tensor.length());
    return RotateLeft.of(tensor, fromIdx).extract(0, idx + 1);
  }
}
