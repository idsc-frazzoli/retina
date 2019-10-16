package ch.ethz.idsc.gokart.core.track;

import ch.ethz.idsc.owl.lane.LaneInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
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

  @Override
  public Tensor controlPoints() {
    return mid;
  }

  @Override
  public Tensor midLane() {
    return mid;
  }

  @Override
  public Tensor leftBoundary() {
    return left;
  }

  @Override
  public Tensor rightBoundary() {
    return right;
  }

  @Override
  public Tensor margins() {
    return margins;
  }

  public LaneInterface unitless() {
    return new LaneInterface() {
      @Override
      public Tensor controlPoints() {
        return Tensor.of(mid.stream().map(PoseHelper::toUnitless));
      }

      @Override
      public Tensor midLane() {
        return Tensor.of(mid.stream().map(PoseHelper::toUnitless));
      }

      @Override
      public Tensor leftBoundary() {
        return Tensor.of(left.stream().map(PoseHelper::toUnitless));
      }

      @Override
      public Tensor rightBoundary() {
        return Tensor.of(right.stream().map(PoseHelper::toUnitless));
      }

      @Override
      public Tensor margins() {
        return margins.map(Magnitude.METER);
      }
    };
  }
}
