// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pure;

import java.util.Optional;
import java.util.stream.IntStream;

import ch.ethz.idsc.retina.util.curve.PeriodicExtract;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Clip;

public enum CurveUtils {
  ;
  public static final int NO_MATCH = -1;

  public static int closestCloserThan(Tensor tensor, Scalar dist) {
    int best = NO_MATCH;
    for (int index = 0; index < tensor.length(); ++index) {
      Scalar norm = Norm._2.of(tensor.get(index)); // vector in local coordinates
      if (Scalars.lessThan(norm, dist)) {
        dist = norm;
        best = index;
      }
    }
    return best;
  }

  public static Optional<Tensor> getAheadTrail(Tensor tensor, Scalar dist) {
    int index = closestCloserThan(tensor, dist);
    if (index != NO_MATCH) {
      int length = tensor.length();
      return Optional.of(Tensor.of( //
          IntStream.range(index, index + length / 2) //
              .map(i -> i % length) //
              .mapToObj(tensor::get)));
    }
    return Optional.empty();
  }

  public static Optional<Tensor> interpolate(Tensor beacons, final int index, Scalar distance) {
    PeriodicExtract periodicExtract = new PeriodicExtract(beacons);
    for (int count = 0; count < beacons.length(); ++count) {
      Tensor next = periodicExtract.get(index + count);
      Scalar hi = Norm._2.of(next);
      if (Scalars.lessEquals(distance, hi)) {
        Tensor prev = periodicExtract.get(index + count - 1);
        Scalar lo = Norm._2.of(prev);
        Clip clip = Clip.function(lo, hi);
        if (clip.isInside(distance)) {
          Scalar lambda = clip.rescale(distance);
          Interpolation interpolation = LinearInterpolation.of(Tensors.of(prev, next));
          return Optional.of(interpolation.get(Tensors.of(lambda)));
        }
      }
    }
    return Optional.empty();
  }
}
