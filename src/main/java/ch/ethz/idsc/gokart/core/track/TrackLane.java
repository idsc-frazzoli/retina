package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;

public class TrackLane implements LaneInterface {
  private final Tensor mid;
  private final Tensor margins;
  private final Tensor left;
  private final Tensor right;

  public TrackLane(BSplineTrack track, Tensor domain) {
    // this is not accurate for large changes in radius
    Tensor middle = domain.map(track::getPositionXY);
    Tensor normal = domain.map(prog -> track.getLeftDirectionXY(prog).multiply(track.getRadius(prog)));
    Tensor lineL = middle.add(normal);
    Tensor lineR = middle.subtract(normal);
    Tensor heading = Tensor.of(domain.map(track::getDerivationXY).stream().map(ArcTan2D::of).map(Tensors::of));
    margins = domain.map(track::getRadius).unmodifiable();
    mid = Join.of(1, middle, heading).unmodifiable();
    left = Join.of(1, lineL, heading).unmodifiable();
    right = Join.of(1, lineR, heading).unmodifiable();
  }

  public Tensor controlPoints() {
    return mid;
  }

  public Tensor midLane() {
    return mid;
  }

  public Tensor leftBoundary() {
    return left;
  }

  public Tensor rightBoundary() {
    return right;
  }

  public Tensor margins() {
    return margins;
  }
}
